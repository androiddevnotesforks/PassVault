package com.mrntlu.PassVault.Online;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import es.dmoral.toasty.Toasty;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;
import com.mrntlu.PassVault.MainActivity;
import com.mrntlu.PassVault.Online.Adapters.OnlineRVAdapter;
import com.mrntlu.PassVault.Online.Viewmodels.OnlineViewModel;
import com.mrntlu.PassVault.R;
import com.parse.ParseObject;
import com.parse.ParseUser;
import java.util.ArrayList;

public class FragmentOnlineStorage extends Fragment {

    private View v;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private OnlineRVAdapter onlineRVAdapter;
    private OnlineViewModel viewModel;
    private OnlineDialog dialogClass;
    private ProgressBar progressBar;

    public static FragmentOnlineStorage newInstance() {
        return new FragmentOnlineStorage();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.online_add_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item!=null && dialogClass!=null && viewModel!=null){
            switch (item.getItemId()){
                case R.id.addOnline:
                    dialogClass.showAddDialog();
                    return true;
                case R.id.syncOnline:
                    initRecyclerView();
                    Toasty.info(getContext(),"Synced.", Toast.LENGTH_SHORT).show();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final ParseUser user=ParseUser.getCurrentUser();
        if (user==null){
            startActivity(new Intent(getActivity(),MainActivity.class));
        }

        setHasOptionsMenu(true);

        v=inflater.inflate(R.layout.fragment_online_storage, container, false);
        recyclerView=(RecyclerView)v.findViewById(R.id.onlineRecycler);
        searchView=(SearchView)v.findViewById(R.id.onlineSearch);
        progressBar=(ProgressBar)v.findViewById(R.id.progressBar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        viewModel=ViewModelProviders.of(getActivity()).get(OnlineViewModel.class);
        viewModel.initOnlineObjects(progressBar);

        initRecyclerView();

        viewModel.getOnlineObjects().observe(getViewLifecycleOwner(), new Observer<ArrayList<ParseObject>>() {
            @Override
            public void onChanged(ArrayList<ParseObject> parseObjects) {
                if (onlineRVAdapter == null) {
                    initRecyclerView();
                } else {
                    onlineRVAdapter.notifyDataSetChanged();
                }
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
               if (!b){
                    initRecyclerView();
               }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                onlineRVAdapter=new OnlineRVAdapter(viewModel.searchOnlineObjects(s).getValue(),getContext(),viewModel,true);
                recyclerView.setAdapter(onlineRVAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.trim().length()==0){
                    initRecyclerView();
                }
                return false;
            }
        });

        dialogClass=new OnlineDialog(viewModel.getOnlineObjects().getValue(),getContext(),viewModel);

        return v;
    }

    private void initRecyclerView(){
        onlineRVAdapter=new OnlineRVAdapter(viewModel.getOnlineObjects().getValue(),getContext(),viewModel);
        recyclerView.setAdapter(onlineRVAdapter);
    }
}
