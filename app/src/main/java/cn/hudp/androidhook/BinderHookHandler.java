package cn.hudp.androidhook;

import android.content.ClipData;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 伪造剪切版服务对象
 * Created by HuDP on 2016/5/23.
 */
public class BinderHookHandler implements InvocationHandler {
    private static final String TAG = "BinderHookHander";
    Object base;//原始的Service对象（IInterface)

    public BinderHookHandler(IBinder base, Class<?> stubClass) {
        try {
            Method asInterfaceMethod = stubClass.getDeclaredMethod("asInterface", IBinder.class);
            this.base = asInterfaceMethod.invoke(null, base);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("hooked failed!");
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if("getPrimaryClip".equals(method.getName())){
            Log.e(TAG, "hook getPrimaryClip");
            return ClipData.newPlainText(null,"you are hooked");
        }
        if("hasPrimaryClip".equals(method.getName())){
            return true;
        }
        return method.invoke(base,args);
    }
}
