import { getStatusBadgeClass } from '../../utils/statusColors';

export default function Badge({ text }) {
  if (!text) return null;
  const badgeClass = getStatusBadgeClass(text);
  return (
    <span className={`badge ${badgeClass}`}>
      {text.replace(/_/g, ' ')}
    </span>
  );
}
