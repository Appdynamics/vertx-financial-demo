package com.appdynamics.reactivetrade.persist;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by trader on 8/13/14.
 */
public class StoredProcStatement implements CallableStatement {

    private Connection conn;

    public StoredProcStatement(Connection conn) {
        this.conn = conn;
    }

    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {

    }

    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {

    }

    public boolean wasNull() throws SQLException {
        return false;
    }

    public String getString(int parameterIndex) throws SQLException {
        return null;
    }

    public boolean getBoolean(int parameterIndex) throws SQLException {
        return false;
    }

    public byte getByte(int parameterIndex) throws SQLException {
        return 0;
    }

    public short getShort(int parameterIndex) throws SQLException {
        return 0;
    }

    public int getInt(int parameterIndex) throws SQLException {
        return 0;
    }

    public long getLong(int parameterIndex) throws SQLException {
        return 0;
    }

    public float getFloat(int parameterIndex) throws SQLException {
        return 0;
    }

    public double getDouble(int parameterIndex) throws SQLException {
        return 0;
    }

    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        return null;
    }

    public byte[] getBytes(int parameterIndex) throws SQLException {
        return new byte[0];
    }

    public Date getDate(int parameterIndex) throws SQLException {
        return null;
    }

    public Time getTime(int parameterIndex) throws SQLException {
        return null;
    }

    public Timestamp getTimestamp(int parameterIndex) throws SQLException {
        return null;
    }

    public Object getObject(int parameterIndex) throws SQLException {
        return null;
    }

    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        return null;
    }

    public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
        return null;
    }

    public Ref getRef(int parameterIndex) throws SQLException {
        return null;
    }

    public Blob getBlob(int parameterIndex) throws SQLException {
        return null;
    }

    public Clob getClob(int parameterIndex) throws SQLException {
        return null;
    }

    public Array getArray(int parameterIndex) throws SQLException {
        return null;
    }

    public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        return null;
    }

    public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        return null;
    }

    public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        return null;
    }

    public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {

    }

    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {

    }

    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {

    }

    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {

    }

    public URL getURL(int parameterIndex) throws SQLException {
        return null;
    }

    public void setURL(String parameterName, URL val) throws SQLException {

    }

    public void setNull(String parameterName, int sqlType) throws SQLException {

    }

    public void setBoolean(String parameterName, boolean x) throws SQLException {

    }

    public void setByte(String parameterName, byte x) throws SQLException {

    }

    public void setShort(String parameterName, short x) throws SQLException {

    }

    public void setInt(String parameterName, int x) throws SQLException {

    }

    public void setLong(String parameterName, long x) throws SQLException {

    }

    public void setFloat(String parameterName, float x) throws SQLException {

    }

    public void setDouble(String parameterName, double x) throws SQLException {

    }

    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {

    }

    public void setString(String parameterName, String x) throws SQLException {

    }

    public void setBytes(String parameterName, byte[] x) throws SQLException {

    }

    public void setDate(String parameterName, Date x) throws SQLException {

    }

    public void setTime(String parameterName, Time x) throws SQLException {

    }

    public void setTimestamp(String parameterName, Timestamp x) throws SQLException {

    }

    public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {

    }

    public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {

    }

    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {

    }

    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {

    }

    public void setObject(String parameterName, Object x) throws SQLException {

    }

    public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {

    }

    public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {

    }

    public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {

    }

    public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {

    }

    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {

    }

    public String getString(String parameterName) throws SQLException {
        return null;
    }

    public boolean getBoolean(String parameterName) throws SQLException {
        return false;
    }

    public byte getByte(String parameterName) throws SQLException {
        return 0;
    }

    public short getShort(String parameterName) throws SQLException {
        return 0;
    }

    public int getInt(String parameterName) throws SQLException {
        return 0;
    }

    public long getLong(String parameterName) throws SQLException {
        return 0;
    }

    public float getFloat(String parameterName) throws SQLException {
        return 0;
    }

    public double getDouble(String parameterName) throws SQLException {
        return 0;
    }

    public byte[] getBytes(String parameterName) throws SQLException {
        return new byte[0];
    }

    public Date getDate(String parameterName) throws SQLException {
        return null;
    }

    public Time getTime(String parameterName) throws SQLException {
        return null;
    }

    public Timestamp getTimestamp(String parameterName) throws SQLException {
        return null;
    }

    public Object getObject(String parameterName) throws SQLException {
        return null;
    }

    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        return null;
    }

    public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
        return null;
    }

    public Ref getRef(String parameterName) throws SQLException {
        return null;
    }

    public Blob getBlob(String parameterName) throws SQLException {
        return null;
    }

    public Clob getClob(String parameterName) throws SQLException {
        return null;
    }

    public Array getArray(String parameterName) throws SQLException {
        return null;
    }

    public Date getDate(String parameterName, Calendar cal) throws SQLException {
        return null;
    }

    public Time getTime(String parameterName, Calendar cal) throws SQLException {
        return null;
    }

    public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        return null;
    }

    public URL getURL(String parameterName) throws SQLException {
        return null;
    }

    public RowId getRowId(int parameterIndex) throws SQLException {
        return null;
    }

    public RowId getRowId(String parameterName) throws SQLException {
        return null;
    }

    public void setRowId(String parameterName, RowId x) throws SQLException {

    }

    public void setNString(String parameterName, String value) throws SQLException {

    }

    public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {

    }

    public void setNClob(String parameterName, NClob value) throws SQLException {

    }

    public void setClob(String parameterName, Reader reader, long length) throws SQLException {

    }

    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {

    }

    public void setNClob(String parameterName, Reader reader, long length) throws SQLException {

    }

    public NClob getNClob(int parameterIndex) throws SQLException {
        return null;
    }

    public NClob getNClob(String parameterName) throws SQLException {
        return null;
    }

    public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {

    }

    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        return null;
    }

    public SQLXML getSQLXML(String parameterName) throws SQLException {
        return null;
    }

    public String getNString(int parameterIndex) throws SQLException {
        return null;
    }

    public String getNString(String parameterName) throws SQLException {
        return null;
    }

    public Reader getNCharacterStream(int parameterIndex) throws SQLException {
        return null;
    }

    public Reader getNCharacterStream(String parameterName) throws SQLException {
        return null;
    }

    public Reader getCharacterStream(int parameterIndex) throws SQLException {
        return null;
    }

    public Reader getCharacterStream(String parameterName) throws SQLException {
        return null;
    }

    public void setBlob(String parameterName, Blob x) throws SQLException {

    }

    public void setClob(String parameterName, Clob x) throws SQLException {

    }

    public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {

    }

    public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {

    }

    public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {

    }

    public void setAsciiStream(String parameterName, InputStream x) throws SQLException {

    }

    public void setBinaryStream(String parameterName, InputStream x) throws SQLException {

    }

    public void setCharacterStream(String parameterName, Reader reader) throws SQLException {

    }

    public void setNCharacterStream(String parameterName, Reader value) throws SQLException {

    }

    public void setClob(String parameterName, Reader reader) throws SQLException {

    }

    public void setBlob(String parameterName, InputStream inputStream) throws SQLException {

    }

    public void setNClob(String parameterName, Reader reader) throws SQLException {

    }

    public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
        return null;
    }

    public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
        return null;
    }

    public ResultSet executeQuery() throws SQLException {
        return null;
    }

    public int executeUpdate() throws SQLException {
        return 0;
    }

    public void setNull(int parameterIndex, int sqlType) throws SQLException {

    }

    public void setBoolean(int parameterIndex, boolean x) throws SQLException {

    }

    public void setByte(int parameterIndex, byte x) throws SQLException {

    }

    public void setShort(int parameterIndex, short x) throws SQLException {

    }

    public void setInt(int parameterIndex, int x) throws SQLException {

    }

    public void setLong(int parameterIndex, long x) throws SQLException {

    }

    public void setFloat(int parameterIndex, float x) throws SQLException {

    }

    public void setDouble(int parameterIndex, double x) throws SQLException {

    }

    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {

    }

    public void setString(int parameterIndex, String x) throws SQLException {

    }

    public void setBytes(int parameterIndex, byte[] x) throws SQLException {

    }

    public void setDate(int parameterIndex, Date x) throws SQLException {

    }

    public void setTime(int parameterIndex, Time x) throws SQLException {

    }

    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {

    }

    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {

    }

    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {

    }

    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {

    }

    public void clearParameters() throws SQLException {

    }

    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {

    }

    public void setObject(int parameterIndex, Object x) throws SQLException {

    }

    public boolean execute() throws SQLException {
        return false;
    }

    public void addBatch() throws SQLException {

    }

    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {

    }

    public void setRef(int parameterIndex, Ref x) throws SQLException {

    }

    public void setBlob(int parameterIndex, Blob x) throws SQLException {

    }

    public void setClob(int parameterIndex, Clob x) throws SQLException {

    }

    public void setArray(int parameterIndex, Array x) throws SQLException {

    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return null;
    }

    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {

    }

    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {

    }

    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {

    }

    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {

    }

    public void setURL(int parameterIndex, URL x) throws SQLException {

    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        return null;
    }

    public void setRowId(int parameterIndex, RowId x) throws SQLException {

    }

    public void setNString(int parameterIndex, String value) throws SQLException {

    }

    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {

    }

    public void setNClob(int parameterIndex, NClob value) throws SQLException {

    }

    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {

    }

    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {

    }

    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {

    }

    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {

    }

    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {

    }

    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {

    }

    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {

    }

    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {

    }

    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {

    }

    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {

    }

    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {

    }

    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {

    }

    public void setClob(int parameterIndex, Reader reader) throws SQLException {

    }

    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {

    }

    public void setNClob(int parameterIndex, Reader reader) throws SQLException {

    }

    public ResultSet executeQuery(String sql) throws SQLException {
        return null;
    }

    public int executeUpdate(String sql) throws SQLException {
        return 0;
    }

    public void close() throws SQLException {

    }

    public int getMaxFieldSize() throws SQLException {
        return 0;
    }

    public void setMaxFieldSize(int max) throws SQLException {

    }

    public int getMaxRows() throws SQLException {
        return 0;
    }

    public void setMaxRows(int max) throws SQLException {

    }

    public void setEscapeProcessing(boolean enable) throws SQLException {

    }

    public int getQueryTimeout() throws SQLException {
        return 0;
    }

    public void setQueryTimeout(int seconds) throws SQLException {

    }

    public void cancel() throws SQLException {

    }

    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    public void clearWarnings() throws SQLException {

    }

    public void setCursorName(String name) throws SQLException {

    }

    public boolean execute(String sql) throws SQLException {
        return false;
    }

    public ResultSet getResultSet() throws SQLException {
        return null;
    }

    public int getUpdateCount() throws SQLException {
        return 0;
    }

    public boolean getMoreResults() throws SQLException {
        return false;
    }

    public void setFetchDirection(int direction) throws SQLException {

    }

    public int getFetchDirection() throws SQLException {
        return 0;
    }

    public void setFetchSize(int rows) throws SQLException {

    }

    public int getFetchSize() throws SQLException {
        return 0;
    }

    public int getResultSetConcurrency() throws SQLException {
        return 0;
    }

    public int getResultSetType() throws SQLException {
        return 0;
    }

    public void addBatch(String sql) throws SQLException {

    }

    public void clearBatch() throws SQLException {

    }

    public int[] executeBatch() throws SQLException {
        return new int[0];
    }

    public Connection getConnection() throws SQLException {
        return conn;
    }

    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        return null;
    }

    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return 0;
    }

    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return 0;
    }

    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return 0;
    }

    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        try {
            Thread.sleep(15);
        } catch (InterruptedException ie) {

        }

        return false;
    }

    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return false;
    }

    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return false;
    }

    public int getResultSetHoldability() throws SQLException {
        return 0;
    }

    public boolean isClosed() throws SQLException {
        return false;
    }

    public void setPoolable(boolean poolable) throws SQLException {

    }

    public boolean isPoolable() throws SQLException {
        return false;
    }

    public void closeOnCompletion() throws SQLException {

    }

    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
