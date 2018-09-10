package info.camposha.retrofitlistviewjson;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {


    class Spacecraft {
        /*
        INSTANCE FIELDS
         */
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("propellant")
        private String propellant;
        @SerializedName("imageurl")
        private String imageURL;
        @SerializedName("technologyexists")
        private int technologyExists;

        public Spacecraft(int id, String name, String propellant, String imageURL, int technologyExists) {
            this.id = id;
            this.name = name;
            this.propellant = propellant;
            this.imageURL = imageURL;
            this.technologyExists = technologyExists;
        }

        /*
         *GETTERS AND SETTERS
         */
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getPropellant() {
            return propellant;
        }

        public String getImageURL() {
            return imageURL;
        }

        public int getTechnologyExists() {
            return technologyExists;
        }

        /*
        TOSTRING
         */
        @Override
        public String toString() {
            return name;
        }
    }

    interface MyAPIService {

        @GET("/Oclemy/SampleJSON/338d9585/spacecrafts.json")
        Call<List<Spacecraft>> getSpacecrafts();
    }

    static class RetrofitClientInstance {

        private static Retrofit retrofit;
        private static final String BASE_URL = "https://raw.githubusercontent.com/";

        public static Retrofit getRetrofitInstance() {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }
    }

    class ListViewAdapter extends BaseAdapter{

        private List<Spacecraft> spacecrafts;
        private Context context;

        public ListViewAdapter(Context context,List<Spacecraft> spacecrafts){
            this.context = context;
            this.spacecrafts = spacecrafts;
        }

        @Override
        public int getCount() {
            return spacecrafts.size();
        }

        @Override
        public Object getItem(int pos) {
            return spacecrafts.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if(view==null)
            {
                view=LayoutInflater.from(context).inflate(R.layout.model,viewGroup,false);
            }

            TextView nameTxt = view.findViewById(R.id.nameTextView);
            TextView txtPropellant = view.findViewById(R.id.propellantTextView);
            CheckBox chkTechExists = view.findViewById(R.id.myCheckBox);
            ImageView spacecraftImageView = view.findViewById(R.id.spacecraftImageView);

            final Spacecraft thisSpacecraft= spacecrafts.get(position);

            nameTxt.setText(thisSpacecraft.getName());
            txtPropellant.setText(thisSpacecraft.getPropellant());
            chkTechExists.setChecked( thisSpacecraft.getTechnologyExists()==1);
            chkTechExists.setEnabled(false);

            if(thisSpacecraft.getImageURL() != null && thisSpacecraft.getImageURL().length()>0)
            {
                Picasso.get().load(thisSpacecraft.getImageURL()).placeholder(R.drawable.placeholder).into(spacecraftImageView);
            }else {
                Toast.makeText(context, "Empty Image URL", Toast.LENGTH_LONG).show();
                Picasso.get().load(R.drawable.placeholder).into(spacecraftImageView);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, thisSpacecraft.getName(), Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }
    }

    private ListViewAdapter adapter;
    private ListView mListView;
    ProgressBar myProgressBar;

    private void populateListView(List<Spacecraft> spacecraftList) {
        mListView = findViewById(R.id.mListView);
        adapter = new ListViewAdapter(this,spacecraftList);
        mListView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ProgressBar myProgressBar= findViewById(R.id.myProgressBar);
        myProgressBar.setIndeterminate(true);
        myProgressBar.setVisibility(View.VISIBLE);

        /*Create handle for the RetrofitInstance interface*/
        MyAPIService myAPIService = RetrofitClientInstance.getRetrofitInstance().create(MyAPIService.class);

        Call<List<Spacecraft>> call = myAPIService.getSpacecrafts();
        call.enqueue(new Callback<List<Spacecraft>>() {

            @Override
            public void onResponse(Call<List<Spacecraft>> call, Response<List<Spacecraft>> response) {
                myProgressBar.setVisibility(View.GONE);
                populateListView(response.body());
            }
            @Override
            public void onFailure(Call<List<Spacecraft>> call, Throwable throwable) {
                myProgressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
