package com.emendo.expensestracker.core.database.dao;

import android.database.Cursor;
import androidx.collection.LongSparseArray;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.emendo.expensestracker.core.database.model.CategoryEntity;
import com.emendo.expensestracker.core.database.model.CategoryFull;
import com.emendo.expensestracker.core.database.model.TransactionEntity;
import com.emendo.expensestracker.core.database.util.Converter;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CategoryDao_Impl extends CategoryDao {
  private final RoomDatabase __db;

  private final EntityDeletionOrUpdateAdapter<CategoryEntity> __deletionAdapterOfCategoryEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  private final EntityUpsertionAdapter<CategoryEntity> __upsertionAdapterOfCategoryEntity;

  public CategoryDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__deletionAdapterOfCategoryEntity = new EntityDeletionOrUpdateAdapter<CategoryEntity>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `category` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, CategoryEntity value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM category";
        return _query;
      }
    };
    this.__upsertionAdapterOfCategoryEntity = new EntityUpsertionAdapter<CategoryEntity>(new EntityInsertionAdapter<CategoryEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT INTO `category` (`id`,`name`,`iconId`,`colorId`,`type`,`currencyId`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, CategoryEntity value) {
        stmt.bindLong(1, value.getId());
        if (value.getName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getName());
        }
        stmt.bindLong(3, value.getIconId());
        stmt.bindLong(4, value.getColorId());
        stmt.bindLong(5, value.getType());
        stmt.bindLong(6, value.getCurrencyId());
      }
    }, new EntityDeletionOrUpdateAdapter<CategoryEntity>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE `category` SET `id` = ?,`name` = ?,`iconId` = ?,`colorId` = ?,`type` = ?,`currencyId` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, CategoryEntity value) {
        stmt.bindLong(1, value.getId());
        if (value.getName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getName());
        }
        stmt.bindLong(3, value.getIconId());
        stmt.bindLong(4, value.getColorId());
        stmt.bindLong(5, value.getType());
        stmt.bindLong(6, value.getCurrencyId());
        stmt.bindLong(7, value.getId());
      }
    });
  }

  @Override
  public Object delete(final CategoryEntity model, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfCategoryEntity.handle(model);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object save(final CategoryEntity model, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfCategoryEntity.upsert(model);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object save(final List<? extends CategoryEntity> models,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfCategoryEntity.upsert(models);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Flow<List<CategoryEntity>> getAll() {
    final String _sql = "SELECT * FROM category";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"category"}, new Callable<List<CategoryEntity>>() {
      @Override
      public List<CategoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIconId = CursorUtil.getColumnIndexOrThrow(_cursor, "iconId");
          final int _cursorIndexOfColorId = CursorUtil.getColumnIndexOrThrow(_cursor, "colorId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfCurrencyId = CursorUtil.getColumnIndexOrThrow(_cursor, "currencyId");
          final List<CategoryEntity> _result = new ArrayList<CategoryEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final CategoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final int _tmpIconId;
            _tmpIconId = _cursor.getInt(_cursorIndexOfIconId);
            final int _tmpColorId;
            _tmpColorId = _cursor.getInt(_cursorIndexOfColorId);
            final int _tmpType;
            _tmpType = _cursor.getInt(_cursorIndexOfType);
            final int _tmpCurrencyId;
            _tmpCurrencyId = _cursor.getInt(_cursorIndexOfCurrencyId);
            _item = new CategoryEntity(_tmpId,_tmpName,_tmpIconId,_tmpColorId,_tmpType,_tmpCurrencyId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<CategoryEntity> getById(final long id) {
    final String _sql = "SELECT * FROM category WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"category"}, new Callable<CategoryEntity>() {
      @Override
      public CategoryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIconId = CursorUtil.getColumnIndexOrThrow(_cursor, "iconId");
          final int _cursorIndexOfColorId = CursorUtil.getColumnIndexOrThrow(_cursor, "colorId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfCurrencyId = CursorUtil.getColumnIndexOrThrow(_cursor, "currencyId");
          final CategoryEntity _result;
          if(_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final int _tmpIconId;
            _tmpIconId = _cursor.getInt(_cursorIndexOfIconId);
            final int _tmpColorId;
            _tmpColorId = _cursor.getInt(_cursorIndexOfColorId);
            final int _tmpType;
            _tmpType = _cursor.getInt(_cursorIndexOfType);
            final int _tmpCurrencyId;
            _tmpCurrencyId = _cursor.getInt(_cursorIndexOfCurrencyId);
            _result = new CategoryEntity(_tmpId,_tmpName,_tmpIconId,_tmpColorId,_tmpType,_tmpCurrencyId);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<CategoryFull>> getCategoriesFull() {
    final String _sql = "SELECT * FROM category";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[]{"transaction","category"}, new Callable<List<CategoryFull>>() {
      @Override
      public List<CategoryFull> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
            final int _cursorIndexOfIconId = CursorUtil.getColumnIndexOrThrow(_cursor, "iconId");
            final int _cursorIndexOfColorId = CursorUtil.getColumnIndexOrThrow(_cursor, "colorId");
            final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
            final int _cursorIndexOfCurrencyId = CursorUtil.getColumnIndexOrThrow(_cursor, "currencyId");
            final LongSparseArray<ArrayList<TransactionEntity>> _collectionTransactions = new LongSparseArray<ArrayList<TransactionEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey = _cursor.getLong(_cursorIndexOfId);
              ArrayList<TransactionEntity> _tmpTransactionsCollection = _collectionTransactions.get(_tmpKey);
              if (_tmpTransactionsCollection == null) {
                _tmpTransactionsCollection = new ArrayList<TransactionEntity>();
                _collectionTransactions.put(_tmpKey, _tmpTransactionsCollection);
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshiptransactionAscomEmendoExpensestrackerCoreDatabaseModelTransactionEntity(_collectionTransactions);
            final List<CategoryFull> _result = new ArrayList<CategoryFull>(_cursor.getCount());
            while(_cursor.moveToNext()) {
              final CategoryFull _item;
              final CategoryEntity _tmpCategory;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpName;
              if (_cursor.isNull(_cursorIndexOfName)) {
                _tmpName = null;
              } else {
                _tmpName = _cursor.getString(_cursorIndexOfName);
              }
              final int _tmpIconId;
              _tmpIconId = _cursor.getInt(_cursorIndexOfIconId);
              final int _tmpColorId;
              _tmpColorId = _cursor.getInt(_cursorIndexOfColorId);
              final int _tmpType;
              _tmpType = _cursor.getInt(_cursorIndexOfType);
              final int _tmpCurrencyId;
              _tmpCurrencyId = _cursor.getInt(_cursorIndexOfCurrencyId);
              _tmpCategory = new CategoryEntity(_tmpId,_tmpName,_tmpIconId,_tmpColorId,_tmpType,_tmpCurrencyId);
              ArrayList<TransactionEntity> _tmpTransactionsCollection_1 = null;
              final long _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpTransactionsCollection_1 = _collectionTransactions.get(_tmpKey_1);
              if (_tmpTransactionsCollection_1 == null) {
                _tmpTransactionsCollection_1 = new ArrayList<TransactionEntity>();
              }
              _item = new CategoryFull(_tmpCategory,_tmpTransactionsCollection_1);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshiptransactionAscomEmendoExpensestrackerCoreDatabaseModelTransactionEntity(
      final LongSparseArray<ArrayList<TransactionEntity>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    // check if the size is too big, if so divide;
    if(_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      LongSparseArray<ArrayList<TransactionEntity>> _tmpInnerMap = new LongSparseArray<ArrayList<TransactionEntity>>(androidx.room.RoomDatabase.MAX_BIND_PARAMETER_CNT);
      int _tmpIndex = 0;
      int _mapIndex = 0;
      final int _limit = _map.size();
      while(_mapIndex < _limit) {
        _tmpInnerMap.put(_map.keyAt(_mapIndex), _map.valueAt(_mapIndex));
        _mapIndex++;
        _tmpIndex++;
        if(_tmpIndex == RoomDatabase.MAX_BIND_PARAMETER_CNT) {
          __fetchRelationshiptransactionAscomEmendoExpensestrackerCoreDatabaseModelTransactionEntity(_tmpInnerMap);
          _tmpInnerMap = new LongSparseArray<ArrayList<TransactionEntity>>(RoomDatabase.MAX_BIND_PARAMETER_CNT);
          _tmpIndex = 0;
        }
      }
      if(_tmpIndex > 0) {
        __fetchRelationshiptransactionAscomEmendoExpensestrackerCoreDatabaseModelTransactionEntity(_tmpInnerMap);
      }
      return;
    }
    StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`sourceId`,`targetId`,`value`,`currencyId`,`type` FROM `transaction` WHERE `targetId` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex ++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "targetId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfSourceId = 1;
      final int _cursorIndexOfTargetId = 2;
      final int _cursorIndexOfValue = 3;
      final int _cursorIndexOfCurrencyId = 4;
      final int _cursorIndexOfType = 5;
      while(_cursor.moveToNext()) {
        final long _tmpKey = _cursor.getLong(_itemKeyIndex);
        ArrayList<TransactionEntity> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final TransactionEntity _item_1;
          final long _tmpId;
          _tmpId = _cursor.getLong(_cursorIndexOfId);
          final long _tmpSourceId;
          _tmpSourceId = _cursor.getLong(_cursorIndexOfSourceId);
          final long _tmpTargetId;
          _tmpTargetId = _cursor.getLong(_cursorIndexOfTargetId);
          final BigDecimal _tmpValue;
          final String _tmp;
          if (_cursor.isNull(_cursorIndexOfValue)) {
            _tmp = null;
          } else {
            _tmp = _cursor.getString(_cursorIndexOfValue);
          }
          _tmpValue = Converter.toBigDecimal(_tmp);
          final int _tmpCurrencyId;
          _tmpCurrencyId = _cursor.getInt(_cursorIndexOfCurrencyId);
          final int _tmpType;
          _tmpType = _cursor.getInt(_cursorIndexOfType);
          _item_1 = new TransactionEntity(_tmpId,_tmpSourceId,_tmpTargetId,_tmpValue,_tmpCurrencyId,_tmpType);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
