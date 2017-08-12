package com.example.iossenac.corre;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by iossenac on 05/08/17.
 */

public class LoginFacebook extends AppCompatActivity{
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private Toast msgErro;

    //private Context context = getApplicationContext();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(AccessToken.getCurrentAccessToken() != null) {
            iniciarApp(AccessToken.getCurrentAccessToken());
        }else {
            setContentView(R.layout.facebook_login_activity);
        }

            callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        iniciarApp(AccessToken.getCurrentAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        msgErro = Toast.makeText(getApplicationContext(),"Login cancelado pelo usuário", Toast.LENGTH_SHORT);
                        msgErro.show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        msgErro = Toast.makeText(getApplicationContext(),"Usuário ou senha inválidos ", Toast.LENGTH_SHORT);
                        msgErro.show();
                    }
                });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void iniciarApp(AccessToken token){
        LoginResult loginResult = new LoginResult(token,token.getPermissions(),token.getDeclinedPermissions());
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application codM
                        Log.e("CASAROSADA", "json face: " + object.toString());

                        final Usuario usuario = new Gson().fromJson(object.toString(), Usuario.class);
                        //Intent intent = new Intent(LoginFacebook.this, MapsActivity.class);
                        Log.e("FOTO", usuario.picture.toString());
                        Intent intent = new Intent(LoginFacebook.this, MapsActivity.class);
                        intent.putExtra("usuario", usuario);

                        startActivity(intent);
                        finish();



                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,picture");
        request.setParameters(parameters);
        request.executeAsync();

        /* GraphRequestAsyncTask request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                Intent intent = new Intent(LoginFacebook.this, MapsActivity.class);
                Usuario usuario = new Usuario();
                Log.e("ID", user.optString("id"));
                usuario.setNome(user.optString("name"));
                usuario.setEmail(user.optString("email"));
                String caminho = "https://graph.facebook.com/"+user.optString("id")+"/picture?type=small";
                try {
                    URL url = new URL(caminho);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                intent.putExtra("usuario", usuario);

                startActivity(intent);

                finish();
            }
        }).executeAsync();*/

    }

}
