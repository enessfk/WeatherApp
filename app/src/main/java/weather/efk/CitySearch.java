package weather.efk;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CitySearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_search);

        final Button button=findViewById(R.id.id_citySearch);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText city=findViewById(R.id.id_cityEdit);
                String citySearched=city.getText().toString();

                Intent returnIntent=new Intent();
                returnIntent.putExtra("result",citySearched);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });


    }
}