export const getStatusBadgeClass = (status) => {
  if (!status) return 'badge-info';
  const upper = status.toUpperCase();

  if (['ACTIVE', 'RESOLVED', 'PAID', 'PUBLISHED', 'CHECKED_OUT', 'COLLECTED', 'APPROVED'].includes(upper)) {
    return 'badge-success';
  }
  if (['PENDING', 'IN_PROGRESS', 'EXPECTED', 'ACKNOWLEDGED', 'NOTIFIED', 'DRAFT', 'CLAIMED', 'ASSIGNED'].includes(upper)) {
    return 'badge-warning';
  }
  if (['OVERDUE', 'CANCELLED', 'DENIED', 'REJECTED', 'CLOSED', 'EXPIRED', 'CRITICAL', 'HIGH'].includes(upper)) {
    return 'badge-danger';
  }
  return 'badge-info';
};
