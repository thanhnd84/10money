package vn.chitieutudo.app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class MainActivity extends Activity {
  private static final String HOST = "appassets.androidplatform.net";
  private static final int REQ_OPEN = 200, REQ_SAVE = 201;
  private WebView browser;
  private ValueCallback<Uri[]> fileCallback;
  private String pendingFileContents;
  private String pendingFileMime;

  @Override public void onCreate(Bundle state) {
    super.onCreate(state);
    browser = new WebView(this);
    setContentView(browser);
    WebSettings settings = browser.getSettings();
    settings.setJavaScriptEnabled(true);
    settings.setDomStorageEnabled(true);
    settings.setAllowFileAccess(false);
    settings.setAllowContentAccess(false);
    settings.setJavaScriptCanOpenWindowsAutomatically(false);
    settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
    browser.addJavascriptInterface(new NativeExports(), "NativeExports");
    browser.setWebViewClient(new WebViewClient() {
      @Override public WebResourceResponse shouldInterceptRequest(WebView v, WebResourceRequest req) {
        Uri u = req.getUrl();
        if ("https".equals(u.getScheme()) && HOST.equals(u.getHost()) && "/index.html".equals(u.getPath())) {
          try {
            InputStream stream = getAssets().open("index.html");
            return new WebResourceResponse("text/html", "UTF-8", stream);
          } catch (Exception ignored) { }
        }
        return new WebResourceResponse("text/plain", "UTF-8", 404, "Not Found", java.util.Collections.emptyMap(), new java.io.ByteArrayInputStream(new byte[0]));
      }
      @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest req) { return true; }
    });
    browser.setWebChromeClient(new WebChromeClient() {
      @Override public boolean onShowFileChooser(WebView w, ValueCallback<Uri[]> cb, FileChooserParams params) {
        if (fileCallback != null) fileCallback.onReceiveValue(null);
        fileCallback = cb;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        try { startActivityForResult(intent, REQ_OPEN); return true; }
        catch (ActivityNotFoundException ex) { fileCallback = null; cb.onReceiveValue(null); return false; }
      }
    });
    browser.loadUrl("https://" + HOST + "/index.html");
  }

  public class NativeExports {
    @JavascriptInterface public void save(String name, String mime, String base64) {
      runOnUiThread(() -> {
        pendingFileContents = base64;
        pendingFileMime = mime;
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mime.startsWith("text/csv") ? "text/csv" : "application/json");
        intent.putExtra(Intent.EXTRA_TITLE, name);
        try { startActivityForResult(intent, REQ_SAVE); }
        catch (ActivityNotFoundException ex) { Toast.makeText(MainActivity.this, "Không mở được hộp thoại lưu file", Toast.LENGTH_SHORT).show(); }
      });
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQ_OPEN) {
      if (fileCallback != null) {
        fileCallback.onReceiveValue(resultCode == RESULT_OK && data != null ? new Uri[]{data.getData()} : null);
        fileCallback = null;
      }
    } else if (requestCode == REQ_SAVE && resultCode == RESULT_OK && data != null) {
      try (OutputStream os = getContentResolver().openOutputStream(data.getData())) {
        os.write(Base64.getDecoder().decode(pendingFileContents));
        Toast.makeText(this, "Đã lưu file", Toast.LENGTH_SHORT).show();
      } catch (Exception ex) {
        Toast.makeText(this, "Lỗi lưu file: " + ex.getMessage(), Toast.LENGTH_LONG).show();
      }
      pendingFileContents = null;
    }
  }
  @Override public void onBackPressed() {
    browser.evaluateJavascript("(function(){let d=document.querySelector('dialog[open]');if(d){d.close();return true;}return false;})()", result -> { if (!"true".equals(result)) new android.app.AlertDialog.Builder(this).setMessage("Thoát ứng dụng?").setNegativeButton("Ở lại",null).setPositiveButton("Thoát",(d,w)->finish()).show(); });
  }
  @Override protected void onDestroy() { if (browser != null) browser.destroy(); super.onDestroy(); }
}
