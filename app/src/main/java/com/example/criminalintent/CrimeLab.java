package com.example.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.criminalintent.database.CrimeDbSchema.CrimeBaseHelper;
import com.example.criminalintent.database.CrimeDbSchema.CrimeDbSchema;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// singleton class
public class CrimeLab {
    private static CrimeLab sCrimeLab; // static variable
    private Context mContext;
    private SQLiteDatabase mDatabase;
    //private List<Crime> mCrimes; // instance variable
    private Map<UUID, Crime> mCrimes;

    public static CrimeLab get(Context context) {
        if(sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }

        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
        //mCrimes = new ArrayList<>();
        mCrimes = new LinkedHashMap<>();
        for(int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i % 2 == 0); // every other one
            //mCrimes.add(crime);
            mCrimes.put(crime.getId(), crime);
        }
    }

    public List<Crime> getCrimes() {
        return new ArrayList<>(mCrimes.values());
        //return null;
    }

    public Crime getCrime(UUID id) {
        return mCrimes.get(id);
        /*
        for(Crime crime : mCrimes) {
            if(crime.getId().equals(id)) {
                return crime;
            }
         }
        return null;
        */
    }

    public void addCrime(Crime c) {
        ContentValues values = getContextValues(c);
        mDatabase.insert(CrimeDbSchema.CrimeTable.NAME, null, values);
    }

    public void updateCrime(Crime crime) {
        String uuidString  = crime.getId().toString();
        ContentValues values = getContextValues(crime);

        mDatabase.update(CrimeDbSchema.CrimeTable.NAME, values, CrimeDbSchema.CrimeTable.Cols.UUID
                        + " = ?", new String[] {uuidString });
    }

    private static ContentValues getContextValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeDbSchema.CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeDbSchema.CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeDbSchema.CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeDbSchema.CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);

        return values;
    }
}
