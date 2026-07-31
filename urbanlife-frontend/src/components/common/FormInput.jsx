export default function FormInput({ label, name, type = 'text', value, onChange, placeholder, options, required = false }) {
  return (
    <div className="form-group">
      {label && <label className="form-label">{label} {required && <span style={{ color: 'var(--danger)' }}>*</span>}</label>}
      {type === 'select' ? (
        <select className="form-control" name={name} value={value} onChange={onChange} required={required}>
          <option value="">Select {label}</option>
          {options?.map((opt, idx) => (
            <option key={idx} value={typeof opt === 'object' ? opt.value : opt}>
              {typeof opt === 'object' ? opt.label : opt.replace(/_/g, ' ')}
            </option>
          ))}
        </select>
      ) : type === 'textarea' ? (
        <textarea
          className="form-control"
          name={name}
          rows={3}
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          required={required}
        />
      ) : (
        <input
          className="form-control"
          type={type}
          name={name}
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          required={required}
        />
      )}
    </div>
  );
}
