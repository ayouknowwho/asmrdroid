package com.ayouknowwho.asmrdroid;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ayouknowwho.asmrdroid.model.AudioRepository;
import com.ayouknowwho.asmrdroid.viewModel.AudioRepositoryViewModel;
import com.ayouknowwho.asmrdroid.viewModel.ImportViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private AudioRepositoryViewModel audioRepositoryViewModel;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Get the ViewModel
        audioRepositoryViewModel = new ViewModelProvider(requireActivity()).get(AudioRepositoryViewModel.class);

        // Get the TextViews
        final TextView opened_text_view = (TextView) view.findViewById(R.id.opened_text_view);
        final TextView corrupted_text_view = (TextView) view.findViewById(R.id.corrupted_text_view);
        final TextView audio_count_text_view = (TextView) view.findViewById(R.id.audio_count_text_view);
        final TextView sample_count_text_view = (TextView) view.findViewById(R.id.sample_count_text_view);

        final Button empty_db_button = (Button) view.findViewById(R.id.empty_db_button);
        empty_db_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioRepositoryViewModel.emptyRepository();
                Toast.makeText(requireContext(), "New database created.", Toast.LENGTH_SHORT).show();
            }
        });

        audioRepositoryViewModel.getUpdated().observe(getViewLifecycleOwner(), uiState -> {
            refreshTextViews(opened_text_view, corrupted_text_view, audio_count_text_view, sample_count_text_view);
            // The below seems to be not needed
            // audioRepositoryViewModel.getUpdated().setValue(false);
        });

        return view;
    }

    private void refreshTextViews(TextView opened_text_view, TextView corrupted_text_view, TextView audio_count_text_view, TextView sample_count_text_view) {
        opened_text_view.setText(audioRepositoryViewModel.getOpened());

        corrupted_text_view.setText(audioRepositoryViewModel.getCorrupted());

        Integer num_files = audioRepositoryViewModel.getNum_files();
        if (num_files == null) {
            audio_count_text_view.setText("Number of files not counted.");
        } else {
            audio_count_text_view.setText(audioRepositoryViewModel.getNum_files().toString() + " files in repository.");
        }

        Integer num_samples = audioRepositoryViewModel.getNum_samples();
        if (num_samples == null) {
            sample_count_text_view.setText("Number of samples not counted.");
        } else {
            sample_count_text_view.setText(audioRepositoryViewModel.getNum_samples().toString() + " samples in repository");
        }
    }
}