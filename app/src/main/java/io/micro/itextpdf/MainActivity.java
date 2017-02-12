package io.micro.itextpdf;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import io.micro.itextpdf.util.PdfUtils;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CHOOSE_PDF = 0x10;

    private Button mSelectButton;
    private Button mConfirmButton;
    private EditText mEditText;
    private PDFView pdfView;
    private String path;
    private String fileName;
    private Paint paint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSelectButton = (Button) findViewById(R.id.btn_select);
        mConfirmButton = (Button) findViewById(R.id.btn_confirm);
        mEditText = (EditText) findViewById(R.id.editText);
        mConfirmButton.setEnabled(false);

        pdfView = (PDFView) findViewById(R.id.pdfView);
        pdfView.useBestQuality(true);

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(36);

        requestPermission();
    }

    public void requestPermission() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (PackageManager.PERMISSION_GRANTED == permission) {

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    10);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            showToast("没有存储写入权限！");
        }
    }

    private void previewPdf(Uri file) {
        pdfView.fromUri(file)
//                .pages(0) // all pages are displayed by default
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .enableDoubletap(true)
                .load();
    }

    private void previewPdf(File file) {
        pdfView.fromFile(file)
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .enableDoubletap(true)
                .load();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CHOOSE_PDF) {
                Uri uri = data.getData();
                path = uri.getPath();
                fileName = path.substring(path.lastIndexOf(File.separator));
                previewPdf(uri);
                mConfirmButton.setEnabled(true);
            }
        } else {
            showToast("没有选择PDF文件");
        }
    }

    public void selectPdf(View view) {
        showFileChooser();
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要添加水印的文件"), REQUEST_CHOOSE_PDF);
        } catch (android.content.ActivityNotFoundException ex) {
            showToast("没有找到文件管理器");
        }
    }

    public void confirm(View view) {
        try {
            String dest = new File(getExternalFilesDir(null), fileName).getAbsolutePath();
            String string = mEditText.getText().toString();
            PdfUtils.writeWaterMark(new FileInputStream(path), new FileOutputStream(dest), string);
            previewPdf(new File(dest));
        } catch (Exception e) {
            e.printStackTrace();
            showToast("操作失败");
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT)
                .show();
    }
}
