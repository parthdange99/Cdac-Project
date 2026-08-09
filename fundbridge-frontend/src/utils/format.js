export function formatMoney(amount) {
  if (amount === null || amount === undefined) return "₹0";
  const num = Number(amount);
  return `₹${num.toLocaleString("en-IN", { maximumFractionDigits: 2 })}`;
}

export function formatDate(value) {
  if (!value) return "-";
  const d = new Date(value);
  if (isNaN(d.getTime())) return value;
  return d.toLocaleDateString("en-IN", {
    year: "numeric",
    month: "short",
    day: "numeric",
  });
}

export function formatDateTime(value) {
  if (!value) return "-";
  const d = new Date(value);
  if (isNaN(d.getTime())) return value;
  return d.toLocaleString("en-IN", {
    year: "numeric",
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export function statusClass(status) {
  const s = (status || "").toString().toLowerCase();
  if (["active", "approved", "accepted", "success", "paid", "disbursed"].includes(s))
    return "badge badge-success";
  if (["pending", "pending_review", "repaying"].includes(s))
    return "badge badge-pending";
  if (["rejected", "cancelled", "failed", "defaulted", "overdue"].includes(s))
    return "badge badge-danger";
  return "badge badge-neutral";
}
