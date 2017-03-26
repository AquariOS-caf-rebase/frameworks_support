/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.support.room.integration.testapp.migration;

import android.content.ContentValues;

import com.android.support.db.SupportSQLiteDatabase;
import com.android.support.room.Dao;
import com.android.support.room.Database;
import com.android.support.room.Entity;
import com.android.support.room.Ignore;
import com.android.support.room.Insert;
import com.android.support.room.PrimaryKey;
import com.android.support.room.Query;
import com.android.support.room.RoomDatabase;

import java.util.List;

@SuppressWarnings("WeakerAccess")
@Database(version = MigrationDb.LATEST_VERSION,
        entities = {MigrationDb.Entity1.class, MigrationDb.Entity2.class})
public abstract class MigrationDb extends RoomDatabase {
    static final int LATEST_VERSION = 6;
    abstract MigrationDao dao();
    @Entity
    static class Entity1 {
        public static final String TABLE_NAME = "Entity1";
        @PrimaryKey
        public int id;
        public String name;
    }

    @Entity
    static class Entity2 {
        public static final String TABLE_NAME = "Entity2";
        @PrimaryKey
        public int id;
        public String addedInV3;
        public String name;
    }

    @Entity
    static class Entity3 { // added in version 4, removed at 6
        public static final String TABLE_NAME = "Entity3";
        @PrimaryKey
        public int id;
        @Ignore //removed at 5
        public String removedInV5;
        public String name;
    }

    @Dao
    interface MigrationDao {
        @Query("SELECT * from Entity1 ORDER BY id ASC")
        List<Entity1> loadAllEntity1s();
        @Query("SELECT * from Entity2 ORDER BY id ASC")
        List<Entity2> loadAllEntity2s();
        @Query("SELECT * from Entity2 ORDER BY id ASC")
        List<Entity2Pojo> loadAllEntity2sAsPojo();
        @Insert
        void insert(Entity2... entity2);
    }

    static class Entity2Pojo extends Entity2 {
    }

    /**
     * not a real dao because database will change.
     */
    static class Dao_V1 {
        final SupportSQLiteDatabase mDb;

        Dao_V1(SupportSQLiteDatabase db) {
            mDb = db;
        }

        public void insertIntoEntity1(int id, String name) {
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("name", name);
            long insertionId = mDb.insert(Entity1.TABLE_NAME, null, values);
            if (insertionId == -1) {
                throw new RuntimeException("test sanity failure");
            }
        }
    }

    /**
     * not a real dao because database will change.
     */
    static class Dao_V2 {
        final SupportSQLiteDatabase mDb;

        Dao_V2(SupportSQLiteDatabase db) {
            mDb = db;
        }

        public void insertIntoEntity2(int id, String name) {
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("name", name);
            long insertionId = mDb.insert(Entity2.TABLE_NAME, null, values);
            if (insertionId == -1) {
                throw new RuntimeException("test sanity failure");
            }
        }
    }
}
