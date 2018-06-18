package com.example.pc_home.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebUrls = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int choosenCeleb = 0;
    String[] answers  = new String[4];
    int locationOfCorrectAnswer = 0;
    Button button1,button2,button3,button4;

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadTask task = new DownloadTask();
        String result = null;
        imageView = (ImageView) findViewById(R.id.imageView);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        try {
            result = task.execute("http://www.posh24.se/kandisar").get();
            Toast.makeText(getApplicationContext(),"getting data!",Toast.LENGTH_SHORT).show();

            String[] spliteResult = result.split("div class=\"listedArticles\">");

            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(spliteResult[0]);

            while (m.find()){
                celebUrls.add(m.group(1));
            }


            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(spliteResult[0]);

            while (m.find()){
                celebNames.add(m.group(1));
            }

            NewQuestion();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public  void selectChosen(View view){
        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
            Toast.makeText(getApplicationContext(),"Correct!",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(),"It was "+ celebNames.get(choosenCeleb),Toast.LENGTH_SHORT).show();
        }

        NewQuestion();
    }

    class ImageDownloader extends AsyncTask<String,Void,Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                Bitmap mybitmap = BitmapFactory.decodeStream((in));
                return mybitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader =  new InputStreamReader(in);
                int data = reader.read();

                while (data != 1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public  void NewQuestion(){

        try {
            Random rand = new Random();
            choosenCeleb = rand.nextInt(celebUrls.size());
            ImageDownloader imageTask = new ImageDownloader();
            Bitmap celebImage = null;
            celebImage = imageTask.execute(celebUrls.get(choosenCeleb)).get();

            imageView.setImageBitmap(celebImage);

            locationOfCorrectAnswer = rand.nextInt(4);
            int inCorrectAnswerLocaiton;
            for (int i=0; i<4; i++){
                if(i==locationOfCorrectAnswer){
                    answers[i] =celebNames.get(choosenCeleb);
                }
                else{
                    inCorrectAnswerLocaiton = rand.nextInt(celebUrls.size());

                    while (inCorrectAnswerLocaiton == choosenCeleb){
                        inCorrectAnswerLocaiton = rand.nextInt(celebUrls.size());
                    }
                    answers[i] = celebNames.get(inCorrectAnswerLocaiton);
                }
            }
            button1.setText(answers[0]);
            button2.setText(answers[1]);
            button3.setText(answers[2]);
            button4.setText(answers[3]);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
}
