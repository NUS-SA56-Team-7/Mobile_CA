package com.stephenphyo.ca_the_memory_game.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.stephenphyo.ca_the_memory_game.R;
import com.stephenphyo.ca_the_memory_game.adapters.FetchGridAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FetchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    /*** Constants & Variables ***/
    String fetchURL;
    int imageMaxCount = 20;
    int currentImageProgress = 0;
    int imageSelectedCount = 0;
    List<String> downloadedImages = new ArrayList<String>();
    List<String> selectedImages = new ArrayList<String>();

    /*** Views & Components ***/
    GridView gridView;
    FetchGridAdapter adapter;
    ProgressBar progressBar;
    TextView textProgress;
    Button buttonStart;
    Target target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch);

        /*** Views & Components ***/
        EditText editFetchURL = findViewById(R.id.edit_fetch_url);
        Button buttonFetch = findViewById(R.id.button_fetch);
        buttonStart = findViewById(R.id.button_start);

        progressBar = findViewById(R.id.progress_bar);
        textProgress = findViewById(R.id.text_progress);

        /*** Initialization ***/
        buttonStart.setEnabled(false);

        Log.d("Initial Downloaded Images", String.valueOf(downloadedImages));

        /*** Shared Preferences ***/
        SharedPreferences sharedPref = getSharedPreferences("fetch", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (sharedPref != null) {
            if (sharedPref.contains("url")) {
                editFetchURL.setText(sharedPref.getString("url", ""));
            }
        }

        /*** Fetch Button ***/
        buttonFetch.setOnClickListener(v -> {
            fetchURL = editFetchURL.getText().toString();
            editor.putString("url", fetchURL);
            editor.apply();

            try {
                downloadedImages.clear();
                selectedImages.clear();
                Log.d("Selected Images", String.valueOf(selectedImages));
                currentImageProgress = 0;
                imageSelectedCount = 0;
                fetchImages(fetchURL);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        /*** Start Button ***/
        buttonStart.setOnClickListener(v -> {
            startDownloadImages();

            Intent intent = new Intent(FetchActivity.this, GuessActivity.class);
            intent.putStringArrayListExtra("selected_images", new ArrayList<>(selectedImages));
            startActivity(intent);
        });

        /*** Grid View ***/
        List<String> showImages = Collections.nCopies(imageMaxCount, "drawable:image");

        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        int rowHeight = (((screenHeight * 17) / 24) / 5) - 60;
        adapter = new FetchGridAdapter(this, showImages, rowHeight);

        gridView = findViewById(R.id.grid_fetch);
        if (gridView != null) {
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
        ImageView imageGridCellOverlay = v.findViewById(R.id.image_grid_cell_overlay);

        try {
            String selectedImage = "file" + String.valueOf(pos) +
                    downloadedImages.get(pos).substring(downloadedImages.get(pos).lastIndexOf('.'));

            if (!selectedImages.contains(selectedImage)) {
                if (imageSelectedCount < 6) {
                    imageSelectedCount++;
                    selectedImages.add(selectedImage);
                    Log.d("Selected Count", String.valueOf(imageSelectedCount));
                    imageGridCellOverlay.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(FetchActivity.this, "Maximum Selectable Images: 6", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                imageSelectedCount--;
                selectedImages.remove(selectedImage);
                imageGridCellOverlay.setVisibility(View.INVISIBLE);
            }

            if (imageSelectedCount == 6) {
                buttonStart.setEnabled(true);
            }
            else {
                buttonStart.setEnabled(false);
            }
        }
        catch (Exception e) { }

        Log.d("Selected Images", String.valueOf(selectedImages));
    }

    @Override
    public void onStop() {
        super.onStop();
        selectedImages.clear();
    }

    /*** Fetching Shuffled 20 Images ***/
    private void fetchImages(String baseURL) throws InterruptedException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                /*** Fetch Image URLs using Jsoup Web Scraping ***/
                Document document = null;
                try {
                    /*** Web Scraping and Fetching Image URLs from specific URL ***/
                    document = Jsoup.connect(baseURL).get();
                    Elements images = document.select("img[src]");

                    List<String> filteredImages = new ArrayList<String>();
                    for (int i = 1; i <= images.size(); i++) {
                        String imageURL = images.get(i - 1).absUrl("src");
                        filteredImages.add(imageURL);
                    }

                    /*** Filter only JPG, JPEG and PNG Images ***/
                    filteredImages = filteredImages.stream()
                            .filter(eachURL -> eachURL.endsWith("png") || eachURL.endsWith("jpg"))
                            .collect(Collectors.toList());

                    /*** Randomly shuffle the Image URLs ***/
                    Collections.shuffle(filteredImages);

                    /*** Retrieve MaxCount (=20) Images from the Shuffled Images ***/
                    if(filteredImages.size() >= imageMaxCount) {
                        filteredImages = filteredImages.subList(0, imageMaxCount);
                    }

                    for (int i = 1; i <= filteredImages.size(); i++) {
                        downloadedImages.add(filteredImages.get(i - 1));
                        currentImageProgress++;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int progress = (int)((currentImageProgress / (float) imageMaxCount) * 100);
                                progressBar.setProgress(progress);
                                textProgress.setText(String.format("Downloading %d of %d images", currentImageProgress, imageMaxCount));
                                if (currentImageProgress == 20) {
                                    textProgress.setText("Download Completed");
                                }
                            }
                        });
                    }
                    Thread.sleep(200);
                }
                catch (IOException e) {

                } catch (InterruptedException e) {

                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
        thread.join();

        ArrayList<String> showImages = new ArrayList<String>(Collections.nCopies(20, "drawable:image"));
        for(int i = 0; i < downloadedImages.size(); i++) {
            showImages.set(i, downloadedImages.get(i));
        }
        adapter.updateDataList(showImages);
    }

    private void startDownloadImages() {
        for (int i = 0; i < downloadedImages.size(); i++) {
            String destFilename = "file" + String.valueOf(i) +
                    downloadedImages.get(i).substring(downloadedImages.get(i).lastIndexOf("."));
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File destFile = new File(dir, destFilename);

            target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    /*** Save the Bitmap to a File ***/
                    try {
                        FileOutputStream outputStream = new FileOutputStream(destFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                        outputStream.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    // Handle Failed Image Download
                    Log.d("Error", "Image download failed");
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            Picasso.get().load(downloadedImages.get(i)).into(target);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FetchActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}