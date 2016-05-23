package cn.hudp.androidhook;

import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by HuDP on 2016/5/23.
 */
public class BinderProxyHookHander implements InvocationHandler {
    private static final String TAG = "BinderProxyHookHander";

    IBinder base;
    Class<?> stub;
    Class<?> iinterface;

    public BinderProxyHookHander(IBinder base) {
        this.base = base;
        try {
            this.stub = Class.forName("android.content.IClipboard$Stub");
            this.iinterface = Class.forName("android.content.IClipboard");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("queryLocalInterface".equals(method.getName())) {
            Log.e(TAG, "hook queryLocalInterface");
            return Proxy.newProxyInstance(proxy.getClass().getClassLoader(),
                    new Class[]{IBinder.class, IInterface.class, this.iinterface}, new BinderHookHandler(base, stub));
        }
        Log.e(TAG, "method: " + method.getName());
        return method.invoke(base, args);
    }
}
