package com.ayouknowwho.asmrdroid;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.ayouknowwho.asmrdroid.interfaces.FileImportStarter;
import com.ayouknowwho.asmrdroid.interfaces.FilePicker;
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
    private ImportViewModel importViewModel;


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
        importViewModel = new ViewModelProvider(requireActivity()).get(ImportViewModel.class);

        // Set up the text view
        final TextView file_pick_text_view = (TextView) view.findViewById(R.id.file_pick_text_view);

        // Set initial file pick text
        refreshTextView(file_pick_text_view);

        // Create a Button Listener for the File Picker
        final Button file_pick_button = (Button) view.findViewById(R.id.file_pick_button);
        file_pick_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FilePicker) requireActivity()).pickFile();
            }
        });

        // Create a Button Listener for the Import File Button
        final Button import_file_button = (Button) view.findViewById(R.id.import_file_button);
        import_file_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FileImportStarter) requireActivity()).importFileToRepository();
            }
        });

        // Listen for changes to the import file uri caused by the picker to update the textview
        importViewModel.getImport_file_live_uri().observe(getViewLifecycleOwner(), uiState -> {
            refreshTextView(file_pick_text_view);
        });

        return view;
    }

    private void refreshTextView(TextView tv) {
        final MutableLiveData<Uri> import_file_live_uri= importViewModel.getImport_file_live_uri();
        final Uri uri = import_file_live_uri.getValue();
        String file_uri_string = uri.toString();
        if (file_uri_string.equals("default.default.default")) {
            tv.setText("Please select a file to import.");
        }
        else {
            tv.setText(file_uri_string);
        }
    }
}