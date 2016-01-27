package com.f0hzz52.rsaencrypt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.Cipher;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String RSA = "RSA";
    private static final int KEY_SIZE = 512;
    public static PublicKey publicKey;
    public static PrivateKey privateKey;

    @Bind(R.id.txtInputMessage)
    EditText txtInputMessage;
    @Bind(R.id.btnEncrypt)
    Button btnEncrypt;
    @Bind(R.id.tvRaw)
    TextView tvRaw;
    @Bind(R.id.btnDecrypt)
    Button btnDecrypt;
    @Bind(R.id.tvOriginal)
    TextView tvOriginal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        try {
            generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    tvRaw.setText(encrypt(txtInputMessage.getText().toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        btnDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    tvOriginal.setText(String.valueOf(decrypt(tvRaw.getText().toString())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void generateKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA);
        keyPairGen.initialize(KEY_SIZE, new SecureRandom());

        KeyPair keyPair = keyPairGen.generateKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    public static byte[] encrypt(String text, PublicKey pubRSA) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, pubRSA);

        return cipher.doFinal(text.getBytes());
    }

    public final static String encrypt(String text) {
        try {
            return byte2hex(encrypt(text, publicKey));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String byte2hex(byte[] encrypt) {
        String hs = "", stmp = "";

        for (int i = 0; i < encrypt.length; i++) {
            stmp = Integer.toHexString(encrypt[i] & 0xFF);

            if (stmp.length() == 1) {
                hs += ("0" + stmp);
            } else {
                hs += stmp;
            }
        }
        return hs.toUpperCase();
    }

    public final static String decrypt(String data) {
        try {
            return new String(decrypt(hex2byte(data.getBytes())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0) {
            throw new IllegalArgumentException("Hello");
        }

        byte[] bytes = new byte[b.length / 2];

        for (int i = 0; i < b.length; i += 2) {
            String item = new String(b, i, 2);

            bytes[i/2] = (byte) Integer.parseInt(item, 16);
        }

        return bytes;
    }


    private static byte[] decrypt(byte[] src) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(src);
    }
}
