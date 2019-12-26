package com.example.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.List;
import java.util.UUID;

public class CrimeListFragment extends Fragment {

    private static final String TAG = "CrimeListFragment";
    private static final int REQUEST_CRIME = 1;

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;

    private Boolean mItemHasChanged = false;
    private UUID mItemChangedID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if(mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            if(mItemHasChanged) {
                int mItemChangedPosition = mAdapter.getCrimeIndex(mItemChangedID);
                Log.d(TAG, "Changed Position: " + mItemChangedPosition);
                mAdapter.notifyItemChanged(mItemChangedPosition);
            }
           //mAdapter.notifyDataSetChanged(); // Update all of the items.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: ");
        if(resultCode != Activity.RESULT_OK) {
            mItemHasChanged = false;
            Log.d(TAG, "Not Ok: " + resultCode);
            return;
        }

        if(requestCode == REQUEST_CRIME) {
            if(data != null) {
                Log.d(TAG, "data != null");
                mItemHasChanged = CrimeActivity.hasCrimeChanged(data);
                mItemChangedID = CrimeActivity.getCrimeId(data);
            }
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;
        android.text.format.DateFormat df = new android.text.format.DateFormat();

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(df.format("EEEE, MMM dd, yyyy", mCrime.getDate()));
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView   = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView    = (TextView) itemView.findViewById(R.id.crime_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);

        }

        @Override
        public void onClick(View view) {
            Intent intent = CrimeActivity.newIntent(getActivity(), mCrime.getId());
            startActivityForResult(intent, REQUEST_CRIME);
            //startActivity(intent);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        // Instantiates the item layout file and view holder.
        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        private int getCrimeIndex(UUID crimeId) {
            for(int i = 0; i < mCrimes.size(); i++) {
                Crime crime = mCrimes.get(i);
                if(crime.getId().equals(crimeId)) {
                    return i;
                }
            }
            return -1;
        }
    }
}

/*
    An Android Adapter is a bridge between UI components and data source that helps
    us to fill data in UI components. It holds the data and send the data to an Adapter view then view can
    take the data from the adapter and show that data on a different view, like RecyclerView.
*/
