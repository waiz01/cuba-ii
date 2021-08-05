package com.example.healthyspotsignin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    String[] member_names;
    TypedArray profile_pics;
    String[] id;
    String[] code;
    String[] group;

    TextView url;
    String email, name, url2;

    List<RowItem> rowItems;
    ListView mylistview;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Drawable drawable= getResources().getDrawable(R.drawable.shj);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(newdrawable);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        rowItems = new ArrayList<RowItem>();
        email = getIntent().getStringExtra("Email");
        name = getIntent().getStringExtra("Name");
        url2 = getIntent().getStringExtra("Image");

        member_names = getResources().getStringArray(R.array.Member_names);


        profile_pics = getResources().obtainTypedArray(R.array.profile_pics);
        url = (TextView) findViewById(R.id.github);

        id = getResources().getStringArray(R.array.id);

        code = getResources().getStringArray(R.array.code);

        group = getResources().getStringArray(R.array.group);

        for (int i = 0; i < member_names.length; i++) {
            RowItem item = new RowItem(member_names[i],
                    profile_pics.getResourceId(i, -1), id[i],
                    code[i], group[i]);
            rowItems.add(item);
        }

        mylistview = (ListView) findViewById(R.id.list);
        CustomAdapter adapter = new CustomAdapter(this, rowItems);
        mylistview.setAdapter(adapter);

        mylistview.setOnItemClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                URL("https://github.com/97nuraiman/healthyspot");
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        String member_name = rowItems.get(position).getMember_name();
        Toast.makeText(getApplicationContext(), "" + member_name,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.signout:
                signOut();
                return true;
            case R.id.home:
                //startActivity(new Intent(AboutActivity.this, ProfileUser.class));

                Intent intent = new Intent(this,ProfileUser.class);

                intent.putExtra("Name", name);
                intent.putExtra("Email", email);
                intent.putExtra("Image", url2);

                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext() , email+ " Signed Out" , Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(AboutActivity.this, MainActivity.class));
                    }
                });
    }

    private void URL(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent (Intent.ACTION_VIEW, uri));
    }
}