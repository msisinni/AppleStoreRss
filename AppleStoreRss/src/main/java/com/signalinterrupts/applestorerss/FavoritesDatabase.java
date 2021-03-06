package com.signalinterrupts.applestorerss;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritesDatabase extends SQLiteOpenHelper {

	// while it would be easy enough to pass the string that turns into a bitmap for these images into the database,
	// I worry that the database may become too large if I am storing those strings too. I may have trouble saving
	// everything during onPause as I intend; Possibly test for further development;
	private static final String TABLE_NAME = "favorite_apps";
	private static final String ROW_ID = "id";
	private static final String TITLE = "title";
	private static final String PRICE = "price";
	private static final String SUMMARY = "summary";
	private static final String COPYRIGHT = "copyright";
	private static final String COMPANY_NAME = "company_name";
	private static final String COMPANY_LINK = "company_link";
	private static final String STORE_LINK = "store_link";
	private static final String DATE = "date";
	private static final String IMAGE_SMALL = "image_small";
	private static final String IMAGE_BIG = "image_big";
	private static final String GENRE = "genre";
	private static final String GENRE_LINK = "genre_link";

	public FavoritesDatabase(Context context) {
		super(context, "notes.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlCreate = String.format("create table %s (%s integer primary key, %s text, %s text, %s text, %s text, %s text, %s text, %s text, %s text, %s text, %s text, %s text, " +
				                                 "%s text)",
				                                TABLE_NAME, ROW_ID, TITLE, PRICE, SUMMARY, COPYRIGHT, COMPANY_NAME, COMPANY_LINK, STORE_LINK, DATE, IMAGE_SMALL, IMAGE_BIG, GENRE, GENRE_LINK);
		db.execSQL(sqlCreate);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	protected void saveFavorites(List<AppleApp> favoriteAppList) {
		SQLiteDatabase database = getWritableDatabase();
		database.delete(TABLE_NAME, null, null);

		if (favoriteAppList != null) {
			int i = 0;
			for (AppleApp appleApp : favoriteAppList) {
				ContentValues values = new ContentValues();

				values.put(ROW_ID, i);
				values.put(TITLE, appleApp.getAppTitle());
				values.put(PRICE, appleApp.getAppPrice());
				values.put(SUMMARY, appleApp.getSummary());
				values.put(COPYRIGHT, appleApp.getCopyright());
				values.put(COMPANY_NAME, appleApp.getCompanyName());
				values.put(COMPANY_LINK, appleApp.getCompanyLink());
				values.put(STORE_LINK, appleApp.getStoreLink());
				values.put(DATE, appleApp.getDate());
				values.put(IMAGE_SMALL, appleApp.getImageUrlSmall());
				values.put(IMAGE_BIG, appleApp.getImageUrlBig());
				values.put(GENRE, appleApp.getGenre());
				values.put(GENRE_LINK, appleApp.getGenreLink());

				database.insert(TABLE_NAME, null, values);
				i++;
			}
		}
	}

	protected Set<AppleApp> loadFavorites() {
		Set<AppleApp> favoriteAppSet = new HashSet<>();

		SQLiteDatabase database = getReadableDatabase();

		Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);

		AppleAppCursor wrappedCursor = new AppleAppCursor(cursor);

		while (wrappedCursor.moveToNext()) {
			AppleApp appleApp = wrappedCursor.getAppleApp();
			appleApp.setFavorite(true);
			favoriteAppSet.add(appleApp);
		}

		cursor.close();

		database.close();

		return favoriteAppSet;
	}

	private static class AppleAppCursor extends CursorWrapper {
		public AppleAppCursor(Cursor cursor) {
			super(cursor);
		}

		AppleApp getAppleApp() {
			if (isBeforeFirst() || isAfterLast()) {
				return null;
			}
			AppleApp.Builder builder = new AppleApp.Builder(getString(getColumnIndex(TITLE)))
					                           .appPrice(getString(getColumnIndex(PRICE)))
					                           .summary(getString(getColumnIndex(SUMMARY)))
					                           .copyright(getString(getColumnIndex(COPYRIGHT)))
					                           .companyName(getString(getColumnIndex(COMPANY_NAME)))
					                           .companyLink(getString(getColumnIndex(COMPANY_LINK)))
					                           .storeLink(getString(getColumnIndex(STORE_LINK)))
					                           .date(getString(getColumnIndex(DATE)))
					                           .imageUrlSmall(getString(getColumnIndex(IMAGE_SMALL)))
					                           .imageUrlBig(getString(getColumnIndex(IMAGE_BIG)))
					                           .genre(getString(getColumnIndex(GENRE)))
					                           .genreLink(getString(getColumnIndex(GENRE_LINK)));

			return builder.build();

		}
	}

}
