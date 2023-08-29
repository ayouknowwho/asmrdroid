package com.ayouknowwho.asmrdroid;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ayouknowwho.asmrdroid.viewModel.ImportViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImportFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ImportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImportFragment newInstance(String param1, String param2) {
        ImportFragment fragment = new ImportFragment();
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
        View view = inflater.inflate(R.layout.fragment_import, container, false);

        // Get the ViewModel
        ImportViewModel importViewModel = new ViewModelProvider(requireActivity()).get(ImportViewModel.class);

        final TextView file_pick_text_view = (TextView) view.findViewById(R.id.file_pick_text_view);
        // Set file pick text
        final Uri file_uri = importViewModel.getImport_file_uri();
        final String file_uri_string = file_uri.toString();
        if (!file_uri_string.equals("default.default.default")) {
            file_pick_text_view.setText(file_uri_string);
        }

        // Create a Button Listener for the File Picker
        final Button file_pick_button = (Button) view.findViewById(R.id.file_pick_button);
        file_pick_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FilePicker) requireActivity()).pickFile();
                final Uri file_uri2 = importViewModel.getImport_file_uri();
                final String file_uri_string2 = file_uri2.toString();
                file_pick_text_view.setText(file_uri_string2);
            }
        });

        // Create a Button Listener for the Import File Button
        final Button import_file_button = (Button) view.findViewById(R.id.import_file_button);
        import_file_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FileImportStarter) requireActivity()).importFile();
            }
        });

        return view;
    }
}