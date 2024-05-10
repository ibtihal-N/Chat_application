package ma.enset.chatactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<UserModel> userModelList;
    RecyclerView recyclerView;
    UsersAdapter usersAdapter;
    String yourName;
    DatabaseReference databaseRerence;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String userName = getIntent().getStringExtra("username");
        getSupportActionBar().setTitle(userName);


        usersAdapter=new UsersAdapter(this, userModelList);
        recyclerView=findViewById(R.id.recycler);

        recyclerView.setAdapter(usersAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseRerence = FirebaseDatabase.getInstance().getReference("users");
        databaseRerence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersAdapter.clear();
                for(DataSnapshot childDataSnapshot: dataSnapshot.getChildren()){
                    String uId = childDataSnapshot.getKey();
                    UserModel userModel= childDataSnapshot.getValue(UserModel.class);
                    if(userModel!=null && userModel.getUserID()!=null && !userModel.getUserID().equals(FirebaseAuth.getInstance().getUid())){
                        usersAdapter.add(userModel);
                    }
                }
                List<UserModel> userModelList=usersAdapter.getUserModelList();
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,SigninActivity.class));
            finish();
            return true;
        }
        return false;
    }
}