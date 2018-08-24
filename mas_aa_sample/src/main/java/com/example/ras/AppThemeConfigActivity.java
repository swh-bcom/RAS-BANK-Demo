package com.example.ras;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ras.util.AppThemeConstants;
import com.example.ras.util.AppThemeUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import yuku.ambilwarna.AmbilWarnaDialog;

public class AppThemeConfigActivity extends BaseActivity {

    Button headerColor, headerTextColor, textColor, btnColor, btnTextColor, uploadLogo,uploadBackground;
    TextView reset, action_bar, title, text, buttonColor, buttonTextColor,appNameText, uploadL, uploadB;
    EditText appName,hexHeaderColor, hexheaderTextColor, hexTextColor, hexBtnColor, hexBtnTextColor;
    View appTheme;
    int defaultColor;

    Toolbar toolbar;

    public static final int REQUEST_PICTURE_FOR_LOGO = 1;

    public static final int REQUEST_PICTURE_FOR_BACKGROUND = 2;

    Activity context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_app_theme_config);
        super.onCreate(savedInstanceState);

        context = this;
        appName = (EditText) findViewById(R.id.set_app_name);
        appName.setSelection(appName.getText().length());
        hexHeaderColor = (EditText) findViewById(R.id.set_header_color);
        hexHeaderColor.setSelection(hexHeaderColor.getText().length());
        hexheaderTextColor = (EditText) findViewById(R.id.set_headerText_color);
        hexheaderTextColor.setSelection(hexheaderTextColor.getText().length());
        hexTextColor = (EditText) findViewById(R.id.set_text_color);
        hexTextColor.setSelection(hexTextColor.getText().length());
        hexBtnColor = (EditText) findViewById(R.id.set_button_color);
        hexBtnColor.setSelection(hexBtnColor.getText().length());
        hexBtnTextColor = (EditText) findViewById(R.id.set_button_text_color);
        hexBtnTextColor.setSelection(hexBtnTextColor.getText().length());

        headerColor = (Button) findViewById(R.id.headercolor);
        headerTextColor = (Button) findViewById(R.id.headertextcolor);
        textColor = (Button) findViewById(R.id.textColor);
        btnColor = (Button) findViewById(R.id.buttonColor);
        btnTextColor = (Button) findViewById(R.id.buttonTextColor);
        uploadLogo = (Button) findViewById(R.id.upload_logo);
        uploadBackground = (Button) findViewById(R.id.upload_background);
        reset = (TextView) findViewById(R.id.reset_theme);
        action_bar = (TextView) findViewById(R.id.headerTextView);
        title = (TextView) findViewById(R.id.headertextTextView);
        text = (TextView) findViewById(R.id.textTextView);
        buttonColor = (TextView) findViewById(R.id.buttonTextView);
        buttonTextColor = (TextView) findViewById(R.id.buttonTextColorTextView);
        appNameText = (TextView) findViewById(R.id.appName);
        uploadL = (TextView) findViewById(R.id.uploadTextView);
        uploadB = (TextView) findViewById(R.id.uploadBackgroundTextView);

        appTheme = (View) findViewById(R.id.appView);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if(AppThemeConstants.ACTION_BAR_COLOR_PICKER !=0 && AppThemeConstants.ACTION_BAR_COLOR_PICKER != getResources().getColor(R.color.colorPrimary)) {
            headerColor.setBackgroundColor(AppThemeConstants.ACTION_BAR_COLOR_PICKER);
            hexHeaderColor.setText(String.format("#%06X", (0xFFFFFF & AppThemeConstants.ACTION_BAR_COLOR_PICKER)));
        }

        if(AppThemeConstants.ACTION_BAR_TITLE_COLOR_PICKER !=0 && AppThemeConstants.ACTION_BAR_TITLE_COLOR_PICKER != getResources().getColor(R.color.white)) {
            headerTextColor.setBackgroundColor(AppThemeConstants.ACTION_BAR_TITLE_COLOR_PICKER);
            hexheaderTextColor.setText(String.format("#%06X", (0xFFFFFF & AppThemeConstants.ACTION_BAR_TITLE_COLOR_PICKER)));
        }

        if(AppThemeConstants.TEXT_COLOR !=0 && AppThemeConstants.TEXT_COLOR != getResources().getColor(R.color.white)) {
            textColor.setBackgroundColor(AppThemeConstants.TEXT_COLOR);
            hexTextColor.setText(String.format("#%06X", (0xFFFFFF & AppThemeConstants.TEXT_COLOR)));
            action_bar.setTextColor(AppThemeConstants.TEXT_COLOR);
            title.setTextColor(AppThemeConstants.TEXT_COLOR);
            text.setTextColor(AppThemeConstants.TEXT_COLOR);
            buttonColor.setTextColor(AppThemeConstants.TEXT_COLOR);
            buttonTextColor.setTextColor(AppThemeConstants.TEXT_COLOR);
            appNameText.setTextColor(AppThemeConstants.TEXT_COLOR);
            uploadL.setTextColor(AppThemeConstants.TEXT_COLOR);
            uploadB.setTextColor(AppThemeConstants.TEXT_COLOR);
        }

        if(AppThemeConstants.BUTTON_COLOR !=0) {
            btnColor.setBackgroundColor(AppThemeConstants.BUTTON_COLOR);
            hexBtnColor.setText(String.format("#%06X", (0xFFFFFF & AppThemeConstants.BUTTON_COLOR)));
        }

        if(AppThemeConstants.BUTTON_TEXT_COLOR !=0 && AppThemeConstants.BUTTON_TEXT_COLOR != getResources().getColor(R.color.white)) {
            btnTextColor.setBackgroundColor(AppThemeConstants.BUTTON_TEXT_COLOR);
            hexBtnTextColor.setText(String.format("#%06X", (0xFFFFFF & AppThemeConstants.BUTTON_TEXT_COLOR)));
        }

        if(AppThemeConstants.APP_NAME != "") {
            appName.setText(AppThemeConstants.APP_NAME);
            appName.setSelection(appName.getText().length());
        }

        if(AppThemeConstants.BACKGROUND_IMAGE != ""){
            byte[] imageAsBytes = Base64.decode(AppThemeConstants.BACKGROUND_IMAGE.getBytes(), Base64.DEFAULT);
            BitmapDrawable d = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
            appTheme.setBackground(d);
        }

        //defaultColor = ContextCompat.getColor(this,R.color.colorPrimary);



        headerColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHeaderColorPicker();
            }
        });


        headerTextColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHeaderTextColorPicker();
            }
        });

        textColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTextColorPicker();
            }
        });

        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openButtonColorPicker();
            }
        });

        btnTextColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openButtonTextColorPicker();
            }
        });

        appName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                AppThemeConstants.APP_NAME = charSequence.toString();
                setTheme();
                AppThemeUtil.updateAppConstants(context);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        hexHeaderColor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String color = charSequence.toString();
                if (color.length() > 1) {
                    try {
                        AppThemeConstants.ACTION_BAR_COLOR_PICKER = Color.parseColor(color);
                        AppThemeConstants.ACTION_BAR_COLOR = Color.parseColor(color);
                        headerColor.setBackgroundColor(AppThemeConstants.ACTION_BAR_COLOR_PICKER);
                        setTheme();
                        AppThemeUtil.updateAppConstants(context);
                    } catch (IllegalArgumentException iae) {
                        // This color string is not valid
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        hexheaderTextColor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String color = charSequence.toString();
                if (color.length() > 1) {
                    try {
                        AppThemeConstants.ACTION_BAR_TITLE_COLOR_PICKER = Color.parseColor(color);
                        AppThemeConstants.ACTION_BAR_TITLE_COLOR = Color.parseColor(color);
                        headerTextColor.setBackgroundColor(AppThemeConstants.ACTION_BAR_TITLE_COLOR_PICKER);
                        setTheme();
                        AppThemeUtil.updateAppConstants(context);
                        // color is a valid color
                    } catch (IllegalArgumentException iae) {
                        // This color string is not valid
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        hexTextColor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String color = charSequence.toString();
                if (color.length() > 1) {
                    try {
                        AppThemeConstants.TEXT_COLOR = Color.parseColor(color);
                        textColor.setBackgroundColor(AppThemeConstants.TEXT_COLOR);
                        action_bar.setTextColor(AppThemeConstants.TEXT_COLOR);
                        title.setTextColor(AppThemeConstants.TEXT_COLOR);
                        text.setTextColor(AppThemeConstants.TEXT_COLOR);
                        buttonColor.setTextColor(AppThemeConstants.TEXT_COLOR);
                        buttonTextColor.setTextColor(AppThemeConstants.TEXT_COLOR);
                        appNameText.setTextColor(AppThemeConstants.TEXT_COLOR);
                        uploadL.setTextColor(AppThemeConstants.TEXT_COLOR);
                        uploadB.setTextColor(AppThemeConstants.TEXT_COLOR);
                        AppThemeUtil.updateAppConstants(context);
                    } catch (IllegalArgumentException iae) {
                        // This color string is not valid
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        hexBtnColor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String color = charSequence.toString();
                if (color.length() > 1) {
                    try {
                        AppThemeConstants.BUTTON_COLOR = Color.parseColor(color);
                        btnColor.setBackgroundColor(AppThemeConstants.BUTTON_COLOR);
                        AppThemeUtil.updateAppConstants(context);
                    } catch (IllegalArgumentException iae) {
                        // This color string is not valid
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        hexBtnTextColor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String color = charSequence.toString();
                if (color.length() > 1) {
                    try {
                        AppThemeConstants.BUTTON_TEXT_COLOR = Color.parseColor(color);
                        btnTextColor.setBackgroundColor(AppThemeConstants.BUTTON_TEXT_COLOR);
                        AppThemeUtil.updateAppConstants(context);
                    } catch (IllegalArgumentException iae) {
                        // This color string is not valid
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        uploadLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create intent for picking a photo from the gallery
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // Bring up gallery to select a photo
                    startActivityForResult(intent,REQUEST_PICTURE_FOR_LOGO );
                }
                //startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), REQUEST_PICTURE_FROM_GALLERY);
            }
        });

        uploadBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create intent for picking a photo from the gallery
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // Bring up gallery to select a photo
                    startActivityForResult(intent,REQUEST_PICTURE_FOR_BACKGROUND );
                }
                //startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), REQUEST_PICTURE_FROM_GALLERY);
            }
        });




        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTheme();
                new AlertDialog.Builder(context)
                        .setTitle("Warning")
                        .setMessage("Are you sure you want to revert to original theme?")
                        .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {
                                // do the acknowledged action, beware, this is run on UI thread
                                toolbar.setBackgroundColor(AppThemeConstants.ACTION_BAR_COLOR);
                                toolbar.setTitleTextColor(AppThemeConstants.ACTION_BAR_TITLE_COLOR);

                                headerColor.setBackgroundColor(AppThemeConstants.ACTION_BAR_COLOR_PICKER);
                                hexHeaderColor.setText(String.format("#%06X", (0xFFFFFF & AppThemeConstants.ACTION_BAR_COLOR_PICKER)));
                                headerTextColor.setBackgroundColor(AppThemeConstants.ACTION_BAR_TITLE_COLOR_PICKER);
                                hexheaderTextColor.setText(String.format("#%06X", (0xFFFFFF & AppThemeConstants.ACTION_BAR_TITLE_COLOR_PICKER)));
                                textColor.setBackgroundColor(AppThemeConstants.TEXT_COLOR);
                                hexTextColor.setText(String.format("#%06X", (0xFFFFFF & AppThemeConstants.TEXT_COLOR)));
                                btnColor.setBackgroundColor(AppThemeConstants.BUTTON_COLOR);
                                hexBtnColor.setText(String.format("#%06X", (0xFFFFFF & AppThemeConstants.BUTTON_COLOR)));
                                btnTextColor.setBackgroundColor(AppThemeConstants.BUTTON_TEXT_COLOR);
                                hexBtnTextColor.setText(String.format("#%06X", (0xFFFFFF & AppThemeConstants.BUTTON_TEXT_COLOR)));

                                action_bar.setTextColor(AppThemeConstants.TEXT_COLOR);
                                title.setTextColor(AppThemeConstants.TEXT_COLOR);
                                text.setTextColor(AppThemeConstants.TEXT_COLOR);
                                buttonColor.setTextColor(AppThemeConstants.TEXT_COLOR);
                                buttonTextColor.setTextColor(AppThemeConstants.TEXT_COLOR);
                                appNameText.setTextColor(AppThemeConstants.TEXT_COLOR);
                                uploadL.setTextColor(AppThemeConstants.TEXT_COLOR);
                                uploadB.setTextColor(AppThemeConstants.TEXT_COLOR);

                                appName.setText(getResources().getString(R.string.app_name));

                                appTheme.setBackground(getResources().getDrawable(R.mipmap.bg_sr));

                            }
                        })
                        .create()
                        .show();

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_PICTURE_FOR_LOGO && resultCode==RESULT_OK && data != null)
        {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            AppThemeConstants.LOGO_ICON = encodeTobase64(bitmap);
            AppThemeUtil.updateAppConstants(context);
        }

        if(requestCode==REQUEST_PICTURE_FOR_BACKGROUND && resultCode==RESULT_OK && data != null)
        {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                BitmapDrawable d = new BitmapDrawable(context.getResources(),bitmap);
                appTheme.setBackground(d);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            AppThemeConstants.BACKGROUND_IMAGE = encodeTobase64(bitmap);
            AppThemeUtil.updateAppConstants(context);
        }
    }

    public void openHeaderColorPicker () {
        ColorDrawable colorDrawable = (ColorDrawable)headerColor.getBackground();
        defaultColor = colorDrawable.getColor();
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(context, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaultColor = color;
                AppThemeConstants.ACTION_BAR_COLOR=defaultColor;
                AppThemeConstants.ACTION_BAR_COLOR_PICKER = defaultColor;
                headerColor.setBackgroundColor(color);
                hexHeaderColor.setText(String.format("#%06X", (0xFFFFFF & AppThemeConstants.ACTION_BAR_COLOR_PICKER)));
                setTheme();
                AppThemeUtil.updateAppConstants(context);
            }
        });
        colorPicker.show();
    }

    public void openHeaderTextColorPicker () {
        ColorDrawable colorDrawable = (ColorDrawable)headerTextColor.getBackground();
        defaultColor = colorDrawable.getColor();
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(context, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaultColor = color;
                AppThemeConstants.ACTION_BAR_TITLE_COLOR=defaultColor;
                AppThemeConstants.ACTION_BAR_TITLE_COLOR_PICKER = defaultColor;
                headerTextColor.setBackgroundColor(color);
                hexheaderTextColor.setText(String.format("#%06X", (0xFFFFFF & AppThemeConstants.ACTION_BAR_TITLE_COLOR_PICKER)));
                setTheme();
                AppThemeUtil.updateAppConstants(context);
            }
        });
        colorPicker.show();
    }

    public void openTextColorPicker () {
        ColorDrawable colorDrawable = (ColorDrawable)textColor.getBackground();
        defaultColor = colorDrawable.getColor();
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(context, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaultColor = color;
                AppThemeConstants.TEXT_COLOR = color;
                action_bar.setTextColor(AppThemeConstants.TEXT_COLOR);
                title.setTextColor(AppThemeConstants.TEXT_COLOR);
                text.setTextColor(AppThemeConstants.TEXT_COLOR);
                buttonColor.setTextColor(AppThemeConstants.TEXT_COLOR);
                buttonTextColor.setTextColor(AppThemeConstants.TEXT_COLOR);
                appNameText.setTextColor(AppThemeConstants.TEXT_COLOR);
                uploadL.setTextColor(AppThemeConstants.TEXT_COLOR);
                textColor.setBackgroundColor(color);
                uploadB.setTextColor(AppThemeConstants.TEXT_COLOR);
                hexTextColor.setText(String.format("#%06X", (0xFFFFFF & AppThemeConstants.TEXT_COLOR)));
                AppThemeUtil.updateAppConstants(context);
            }
        });
        colorPicker.show();
    }

    public void openButtonColorPicker () {
        ColorDrawable colorDrawable = (ColorDrawable)btnColor.getBackground();
        defaultColor = colorDrawable.getColor();
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(context, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaultColor = color;
                btnColor.setBackgroundColor(color);
                AppThemeConstants.BUTTON_COLOR=defaultColor;
                hexBtnColor.setText(String.format("#%06X", (0xFFFFFF & AppThemeConstants.BUTTON_COLOR)));
                AppThemeUtil.updateAppConstants(context);
            }
        });
        colorPicker.show();
    }

    public void openButtonTextColorPicker () {
        ColorDrawable colorDrawable = (ColorDrawable)btnTextColor.getBackground();
        defaultColor = colorDrawable.getColor();
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(context, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaultColor = color;
                btnTextColor.setBackgroundColor(color);
                AppThemeConstants.BUTTON_TEXT_COLOR=defaultColor;
                hexBtnTextColor.setText(String.format("#%06X", (0xFFFFFF & AppThemeConstants.BUTTON_TEXT_COLOR)));
                AppThemeUtil.updateAppConstants(context);
            }
        });
        colorPicker.show();
    }

    // method for bitmap to base64
    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

}
