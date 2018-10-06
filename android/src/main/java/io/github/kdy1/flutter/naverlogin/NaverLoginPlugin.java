package io.github.kdy1.flutter.naverlogin;

import android.annotation.SuppressLint;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * NaverLoginPlugin
 */
public class NaverLoginPlugin implements MethodCallHandler {
    private final Registrar registrar;

    private NaverLoginPlugin(Registrar registrar) {
        this.registrar = registrar;
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "io.github.kdy1/naver_login");
        channel.setMethodCallHandler(new NaverLoginPlugin(registrar));
    }


    @Override
    public void onMethodCall(MethodCall call, final Result result) {
        switch (call.method) {
            case "init": {
                String clientId = call.argument("clientId");
                String clientSecret = call.argument("clientSecret");
                String clientName = call.argument("clientName");

                OAuthLogin.getInstance().init(registrar.activity(), clientId, clientSecret, clientName);
                result.success(null);
            }

            case "start": {
                final OAuthLogin oAuthLogin = OAuthLogin.getInstance();
                @SuppressLint("HandlerLeak")
                OAuthLoginHandler oAuthLoginHandler = new OAuthLoginHandler() {
                    @Override
                    public void run(boolean success) {
                        if (success) {
                            Map<String, Object> res = new HashMap<>();
                            res.put("accessToken", oAuthLogin.getAccessToken(registrar.context()));
                            res.put("refreshToken", oAuthLogin.getRefreshToken(registrar.context()));
                            res.put("expiresAt", oAuthLogin.getExpiresAt(registrar.context()));
                            res.put("tokenType", oAuthLogin.getTokenType(registrar.context()));
                            result.success(res);
                        } else {
                            String errorCode = oAuthLogin.getLastErrorCode(registrar.context()).getCode();
                            String errorDesc = oAuthLogin.getLastErrorDesc(registrar.context());
                            result.error(errorCode, errorDesc, null);
                        }
                    }

                    ;
                };
                OAuthLogin.getInstance().startOauthLoginActivity(registrar.activity(), oAuthLoginHandler);

            }

            default:
                result.notImplemented();
        }
    }
}
