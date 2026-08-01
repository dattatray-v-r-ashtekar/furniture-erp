export const saveRecentId = (key, id) => {
  const existing = JSON.parse(localStorage.getItem('erp_recent_ids') || '{}');
  existing[key] = id;
  localStorage.setItem('erp_recent_ids', JSON.stringify(existing));
};

export const getRecentId = (key) => {
  const existing = JSON.parse(localStorage.getItem('erp_recent_ids') || '{}');
  return existing[key];
};
