package com.emendo.expensestracker.core.database.dao;

import android.database.Cursor;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.emendo.expensestracker.core.database.model.AccountEntity;
import com.emendo.expensestracker.core.database.util.Converter;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
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
public final class AccountDao_Impl extends AccountDao {
  private final RoomDatabase __db;

  private final EntityDeletionOrUpdateAdapter<AccountEntity> __deletionAdapterOfAccountEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  private final SharedSQLiteStatement __preparedStmtOfUpdateBalance;

  private final EntityUpsertionAdapter<AccountEntity> __upsertionAdapterOfAccountEntity;

  public AccountDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__deletionAdapterOfAccountEntity = new EntityDeletionOrUpdateAdapter<AccountEntity>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `account` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, AccountEntity value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM account";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateBalance = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE account SET balance = ? WHERE id = ?";
        return _query;
      }
    };
    this.__upsertionAdapterOfAccountEntity = new EntityUpsertionAdapter<AccountEntity>(new EntityInsertionAdapter<AccountEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT INTO `account` (`id`,`name`,`balance`,`currencyId`,`iconId`,`colorId`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, AccountEntity value) {
        stmt.bindLong(1, value.getId());
        if (value.getName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getName());
        }
        final String _tmp = Converter.fromBigDecimal(value.getBalance());
        if (_tmp == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, _tmp);
        }
        stmt.bindLong(4, value.getCurrencyId());
        stmt.bindLong(5, value.getIconId());
        stmt.bindLong(6, value.getColorId());
      }
    }, new EntityDeletionOrUpdateAdapter<AccountEntity>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE `account` SET `id` = ?,`name` = ?,`balance` = ?,`currencyId` = ?,`iconId` = ?,`colorId` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, AccountEntity value) {
        stmt.bindLong(1, value.getId());
        if (value.getName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getName());
        }
        final String _tmp = Converter.fromBigDecimal(value.getBalance());
        if (_tmp == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, _tmp);
        }
        stmt.bindLong(4, value.getCurrencyId());
        stmt.bindLong(5, value.getIconId());
        stmt.bindLong(6, value.getColorId());
        stmt.bindLong(7, value.getId());
      }
    });
  }

  @Override
  public Object delete(final AccountEntity model, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfAccountEntity.handle(model);
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
  public void updateBalance(final long id, final BigDecimal balance) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateBalance.acquire();
    int _argIndex = 1;
    final String _tmp = Converter.fromBigDecimal(balance);
    if (_tmp == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, _tmp);
    }
    _argIndex = 2;
    _stmt.bindLong(_argIndex, id);
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfUpdateBalance.release(_stmt);
    }
  }

  @Override
  public Object save(final AccountEntity model, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfAccountEntity.upsert(model);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object save(final List<? extends AccountEntity> models,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfAccountEntity.upsert(models);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Flow<List<AccountEntity>> getAll() {
    final String _sql = "SELECT * FROM account";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"account"}, new Callable<List<AccountEntity>>() {
      @Override
      public List<AccountEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBalance = CursorUtil.getColumnIndexOrThrow(_cursor, "balance");
          final int _cursorIndexOfCurrencyId = CursorUtil.getColumnIndexOrThrow(_cursor, "currencyId");
          final int _cursorIndexOfIconId = CursorUtil.getColumnIndexOrThrow(_cursor, "iconId");
          final int _cursorIndexOfColorId = CursorUtil.getColumnIndexOrThrow(_cursor, "colorId");
          final List<AccountEntity> _result = new ArrayList<AccountEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final AccountEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final BigDecimal _tmpBalance;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfBalance)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfBalance);
            }
            _tmpBalance = Converter.toBigDecimal(_tmp);
            final int _tmpCurrencyId;
            _tmpCurrencyId = _cursor.getInt(_cursorIndexOfCurrencyId);
            final int _tmpIconId;
            _tmpIconId = _cursor.getInt(_cursorIndexOfIconId);
            final int _tmpColorId;
            _tmpColorId = _cursor.getInt(_cursorIndexOfColorId);
            _item = new AccountEntity(_tmpId,_tmpName,_tmpBalance,_tmpCurrencyId,_tmpIconId,_tmpColorId);
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
  public Flow<AccountEntity> getById(final long id) {
    final String _sql = "SELECT * FROM account WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"account"}, new Callable<AccountEntity>() {
      @Override
      public AccountEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBalance = CursorUtil.getColumnIndexOrThrow(_cursor, "balance");
          final int _cursorIndexOfCurrencyId = CursorUtil.getColumnIndexOrThrow(_cursor, "currencyId");
          final int _cursorIndexOfIconId = CursorUtil.getColumnIndexOrThrow(_cursor, "iconId");
          final int _cursorIndexOfColorId = CursorUtil.getColumnIndexOrThrow(_cursor, "colorId");
          final AccountEntity _result;
          if(_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final BigDecimal _tmpBalance;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfBalance)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfBalance);
            }
            _tmpBalance = Converter.toBigDecimal(_tmp);
            final int _tmpCurrencyId;
            _tmpCurrencyId = _cursor.getInt(_cursorIndexOfCurrencyId);
            final int _tmpIconId;
            _tmpIconId = _cursor.getInt(_cursorIndexOfIconId);
            final int _tmpColorId;
            _tmpColorId = _cursor.getInt(_cursorIndexOfColorId);
            _result = new AccountEntity(_tmpId,_tmpName,_tmpBalance,_tmpCurrencyId,_tmpIconId,_tmpColorId);
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

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
