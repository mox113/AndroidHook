package cn.hudp.androidhook;

import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    protected Button btn,btnTwo;
    private String text = "666";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hook();
            }
        });
        btnTwo = (Button) findViewById(R.id.btnTwo);
        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hook2();
            }
        });
    }

    private void hook2() {
        Class<?> activity = this.getClass();
        try {
            Field field = activity.getDeclaredField("text");
            field.set(this,"我是反射设置的值");
            Method method = activity.getDeclaredMethod("showToast",String.class);
            method.invoke(this,text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void showToast(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }
    final String CLIPBOARD_SERVICE = "clipboard";
    private void hook() {
        try {
            Class<?> serviceManager = Class.forName("android.os.ServiceManager");
            Method getService  = serviceManager.getDeclaredMethod("getService",String.class);
            IBinder rawBinder = (IBinder) getService.invoke(null,CLIPBOARD_SERVICE);
            IBinder hookedBinder = (IBinder) Proxy.newProxyInstance(serviceManager.getClassLoader(),new Class<?>[]{IBinder.class},new BinderProxyHookHander(rawBinder));

            Field chcheField = serviceManager.getDeclaredField("sCache");
            chcheField.setAccessible(true);
            Map<String,IBinder> cache = (Map) chcheField.get(null);
            cache.put(CLIPBOARD_SERVICE,hookedBinder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
