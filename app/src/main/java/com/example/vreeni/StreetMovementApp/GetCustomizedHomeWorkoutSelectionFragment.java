package com.example.vreeni.StreetMovementApp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by vreee on 20/12/2017.
 */

public class GetCustomizedHomeWorkoutSelectionFragment extends Fragment implements View.OnClickListener {
    private String exerciseI;
    private String exerciseII;
    private String imgEx1;
    private int time;
    private ImageView imageEx1;
    private Workout workout;

    private Bundle bundle;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btn_Continue = (Button) view.findViewById(R.id.btn_predef_homeworkout_SelectionContinue);
        btn_Continue.setOnClickListener(this);

        bundle = getArguments();

        if (null != bundle) {
            exerciseI = bundle.getString("Exercise1");
            exerciseII = bundle.getString("Exercise2");
            imgEx1 = bundle.getString("Image");
            time = bundle.getInt("Time");
            workout = bundle.getParcelable("Workout");
            //maybe here create exercise objects and set the fields?? (exerciseI = new Exercise(); exerciseI.setDescription, setIsCompleted....)
            //then add them to a list of exercise objects?

        }
        TextView ex1 = (TextView) view.findViewById(R.id.ex1);
        ex1.setText(exerciseI);

        TextView ex2 = (TextView) view.findViewById(R.id.ex2);
        ex2.setText(exerciseII);

        TextView timeView = (TextView) view.findViewById(R.id.time);
        timeView.setText(time + " minutes");

        imageEx1 = (ImageView) view.findViewById(R.id.imgEx1);
        Glide
                .with(this)
                .load(imgEx1)
                .override(200, 200) // resizes the image to these dimensions (in pixel). does not respect aspect ratio
                .into(imageEx1);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_get_customized_homeworkout_selection, container, false);

    }

    @Override
    public void onClick(View v) {
        //after pressing continue, send the bundle and show warm-up fragment
        Fragment fragment = null;
        if (v.getId() == R.id.btn_predef_homeworkout_SelectionContinue) {
            fragment = new GetCustomizedHomeWorkout_WarmupFragment();
            if (bundle != null) {
                //putting the object again after removing it first, cause of issues with pause function
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }
}


//        public void queryLevel1HomeWorkout() {
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//            CollectionReference wkquery = db.collection("PredefinedWorkouts");
//            Query query = wkquery.whereEqualTo("Home", true )
//                    .whereEqualTo("Level", "Beginner");
//            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    if(task.isSuccessful()) {
//                        QuerySnapshot qSnap = task.getResult();
//                        if (!qSnap.isEmpty()) {
//                            Log.d("Query Data", String.valueOf(task.getResult().getDocuments().get(0).getData()));
//                            String s = String.valueOf(task.getResult().getDocuments().get(0).getData());
//                            int from = s.indexOf('E');
//                            int to = s.indexOf(',', from+1);
//                            String subS = s.substring(from, to);
//                            //Log.d("Query extract", )
//                            ex1.setText(subS);
//                        } else {
//                            Log.d("Query Data", "Data is not valid");
//
//                        }
//
//                    }
//                }
//            });
//        }






