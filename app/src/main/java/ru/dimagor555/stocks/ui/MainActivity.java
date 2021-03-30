package ru.dimagor555.stocks.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ru.dimagor555.stocks.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}