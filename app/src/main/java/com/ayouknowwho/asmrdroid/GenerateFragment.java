package com.ayouknowwho.asmrdroid;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ayouknowwho.asmrdroid.viewModel.GenerateViewModel;
import com.ayouknowwho.asmrdroid.viewModel.ImportViewModel;
import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GenerateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GenerateFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public GenerateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GenerateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GenerateFragment newInstance(String param1, String param2) {
        GenerateFragment fragment = new GenerateFragment();
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
        View view = inflater.inflate(R.layout.fragment_generate, container, false);

        // Get the ViewModel
        GenerateViewModel generateViewModel = new ViewModelProvider(requireActivity()).get(GenerateViewModel.class);

        // Update the ViewModel externalFilesDir from the Context
        Uri external_files_dir = Uri.parse(requireActivity().getExternalFilesDir(Environment.DIRECTORY_MUSIC).toURI().toString());
        generateViewModel.setExternal_files_dir(external_files_dir);

        // Get information from the ViewModel
        Integer num_minutes_to_generate = generateViewModel.getNum_minutes_to_generate();

        final TextInputEditText input_num_minutes = (TextInputEditText) view.findViewById(R.id.input_num_minutes);
        if (num_minutes_to_generate == null) {
            input_num_minutes.setText("0");
        } else {
            input_num_minutes.setText(num_minutes_to_generate.toString());
        }

        final TextView external_files_dir_text = (TextView) view.findViewById(R.id.external_files_dir_text);
        if (!external_files_dir.equals("default.default.default")) {
            String external_files_dir_string = "New file will be saved to " + external_files_dir.toString();
            external_files_dir_text.setText(external_files_dir_string);
        }

        // Create a text change listener for the number of minutes text area
        input_num_minutes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text_input = input_num_minutes.getText().toString();
                try {
                    Integer int_from_text_input = Integer.parseInt(text_input);
                    generateViewModel.setNum_minutes_to_generate(int_from_text_input);
                } catch (NumberFormatException e) {

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Create a Button Listener for the Generate button
        final Button generate_audio_button = (Button) view.findViewById(R.id.generate_audio_button);
        generate_audio_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String string_for_toast = "Generating " + generateViewModel.getNum_minutes_to_generate().toString() + " minutes of audio.";
                    Toast.makeText(requireContext(), string_for_toast, Toast.LENGTH_SHORT).show();
                } catch (NullPointerException e) {
                    Toast.makeText(requireContext(), "Number of minutes not set.", Toast.LENGTH_SHORT).show();
                }
                ((GenerateAudioStarter) requireActivity()).generateAudioFile();
            }
        });

        return view;
    }
}