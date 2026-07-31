export default function Table({ columns, data, emptyMessage = 'No records found' }) {
  if (!data || data.length === 0) {
    return (
      <div className="card" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
        {emptyMessage}
      </div>
    );
  }

  return (
    <div className="table-wrapper">
      <table className="custom-table">
        <thead>
          <tr>
            {columns.map((col, idx) => (
              <th key={idx}>{col.header}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.map((row, rowIdx) => (
            <tr key={rowIdx}>
              {columns.map((col, colIdx) => (
                <td key={colIdx}>
                  {col.render ? col.render(row) : row[col.accessorKey]}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
