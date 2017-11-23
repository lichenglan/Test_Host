package com.techjumper.polyhome.polyhomebhost.by_function.log;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.techjumper.corelib.utils.Utils;
import com.techjumper.polyhome.polyhomebhost.entity.sql.PolyLog;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

public class PolyLogDbExecutor {

    private volatile static BriteDatabaseHelper mDbHelper;

    private PolyLogDbExecutor() {
    }

    public static BriteDatabaseHelper getHelper() {

        if (mDbHelper == null || mDbHelper.isClosed()) {
            synchronized (PolyLogDbExecutor.class) {
                if (mDbHelper == null || mDbHelper.isClosed()) {
                    SqlBrite sqlBrite = SqlBrite.create();
                    BriteDatabase briteDatabase = sqlBrite.wrapDatabaseHelper(PolyLogDbHelper.create(Utils.appContext)
                            , Schedulers.io());
                    mDbHelper = new BriteDatabaseHelper(briteDatabase);
                }
            }
        }
        return mDbHelper;
    }

    public static class BriteDatabaseHelper {

        private BriteDatabase mDb;

        private BriteDatabaseHelper(BriteDatabase briteDatabase) {
            mDb = briteDatabase;
        }

//        public Observable<AdStat> query(String adId, String type) {
//            return mDb.createQuery(AdStat.TABLE_NAME, AdStat.SELECT_BY_ADID_AND_TYPE, adId, type)
//                    .map(query -> {
//                        Cursor cursor = query.run();
//                        if (cursor == null || !cursor.moveToNext()) {
//                            return null;
//                        }
//                        AdStat adStat = null;
//                        try {
//                            adStat = AdStat.MAPPER.map(cursor);
//                        } finally {
//                            cursor.close();
//                        }
//                        return adStat;
//                    })
//                    .onErrorResumeNext(throwable -> {
//                        try {
//                            deleteAll();
//                        } catch (Exception ignored) {
//                        }
//                        return null;
//                    })
//                    .first();
//        }

        public Observable<List<PolyLog>> queryAll() {
            return mDb.createQuery(PolyLog.TABLE_NAME, PolyLog.SELECT_ALL)
                    .map(SqlBrite.Query::run)
                    .map(cursor -> {
                        List<PolyLog> polyLogs = new ArrayList<>();
                        if (cursor == null) {
                            return polyLogs;
                        }
                        try {
                            while (cursor.moveToNext()) {
                                polyLogs.add(PolyLog.MAPPER.map(cursor));
                            }
                        } finally {
                            cursor.close();
                        }
                        return polyLogs;
                    })
                    .onErrorResumeNext(throwable -> {
                        try {
                            deleteAll();
                        } catch (Exception ignored) {
                        }
                        return null;
                    })
                    .first();
        }

        //        public Observable<Long> insertOrUpdate(String adId, int count, String type) {
//            return query(adId, type)
//                    .map(adStat -> {
//                        if (adStat == null) {
//                            return insert(adId, count, type);
//                        }
//                        return (long) update(adId, count, type);
//                    });
//        }
//
//        public Observable<Long> increase(String adId, String type) {
//            return query(adId, type)
//                    .map(adStat -> {
//                        if (adStat == null) {
//                            return insert(adId, 1, type);
//                        }
//                        return (long) update(adId, adStat.count() + 1L, type);
//                    });
//        }
//
//        public int delete(String adId, String type) {
//            return mDb.delete(AdStat.TABLE_NAME, AdStat.ADID + "=? and " + AdStat.POSITION + " =?", adId, type);
//        }
//
        public boolean deleteAll() {
            return mDb.delete(PolyLog.TABLE_NAME, null) == 1;
        }

        public synchronized void close() {
            try {
                mDb.close();
            } catch (Exception ignored) {
            } finally {
                mDb = null;
            }
        }

        public boolean isClosed() {
            return mDb == null;
        }

        public long insert(String content) {
            return
                    mDb.insert(PolyLog.TABLE_NAME
                            , PolyLog.FACTORY.marshal()
                                    .content(content)
                                    .time(System.currentTimeMillis() / 1000)
                                    .asContentValues()
                    );
        }

    }

}