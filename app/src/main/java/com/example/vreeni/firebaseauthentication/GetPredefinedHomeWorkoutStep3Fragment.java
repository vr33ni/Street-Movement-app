//package com.example.vreeni.firebaseauthentication;
//
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.EventListener;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.FirebaseFirestoreException;
//import com.google.firebase.firestore.Query;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import static com.example.vreeni.firebaseauthentication.User.TAG;
//
///**
// * Created by vreee on 20/12/2017.
// */
//
//public class GetPredefinedHomeWorkoutStep2Fragment extends Fragment implements View.OnClickListener {
//    private Button btnPredefHomeWorkoutBeginner;
//    private Button btnPredefHomeWorkoutIntermed;
//    private Button btnPredefHomeWorkoutAdvanced;
//
//    private TextView test;
//
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        btnPredefHomeWorkoutBeginner = (Button) view.findViewById(R.id.btn_predef_homeworkout_beginner);
//        btnPredefHomeWorkoutBeginner.setOnClickListener(this);
//
//        btnPredefHomeWorkoutIntermed = (Button) view.findViewById(R.id.btn_predef_homeworkout_intermediate);
//        btnPredefHomeWorkoutIntermed.setOnClickListener(this);
//
//        test = (TextView) view.findViewById(R.id.TESTtxtView);
//    }
//
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        return inflater.inflate(R.layout.fragment_get_predefined_homeworkout_step2, container, false);
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        Fragment fragment = null;
//        if (v.getId() == R.id.btn_predef_homeworkout_beginner) {
//            //new fragment for choosing your focus of a predefined home workout
//            //Call a method to get the data.
//         //this.getActivity makes sure the listener only works when in this FragmentActivity
//                queryLevel1HomeWorkout();
//                //getData();
//            } else if (v.getId() == R.id.btn_predef_homeworkout_intermediate) {
//                //new fragment for choosing your focus of a predefined home workout
//            } else if (v.getId() == R.id.btn_predef_homeworkout_advanced) {
//                //new fragment for choosing your level of a predefined home workout
//            }
//            //OR IF CASE = OUTDOORS =>
//      /*  getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container, fragment)
//                .addToBackStack(null)
//                .commit();
//
//        */
//        }
//
//
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
//                            test.setText(subS);
//                        } else {
//                            Log.d("Query Data", "Data is not valid");
//
//                        }
//
//                    }
//                }
//            });
//        }
//
//    }
//
//
//
//
//
