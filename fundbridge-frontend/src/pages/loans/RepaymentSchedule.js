import React, { useCallback, useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../../api/axios";
import { formatMoney, formatDate, formatDateTime, statusClass } from "../../utils/format";
import { loadRazorpayScript } from "../../utils/loadRazorpay";
import { useAuth } from "../../context/AuthContext";

export default function RepaymentSchedule() {
  const { id } = useParams();
  const { user } = useAuth();
  const [schedule, setSchedule] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [payingId, setPayingId] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const res = await api.get(`/loans/${id}/repayment-schedule`);
      setSchedule(res.data.data || []);
    } catch (err) {
      setError(
        err?.response?.data?.message || "Could not load repayment schedule."
      );
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    load();
  }, [load]);

  const payInstallment = async (item) => {
    setPayingId(item.id);
    try {
      const orderRes = await api.post("/payments/create-order", null, {
        params: {
          amount: Number(item.emiAmount),
          description: `EMI #${item.installmentNumber} for loan ${id}`,
        },
      });
      const order = orderRes.data.data;

      const loaded = await loadRazorpayScript();
      if (!loaded || !window.Razorpay) {
        alert("Could not load the payment gateway. Please try again.");
        setPayingId(null);
        return;
      }

      const options = {
        key: order.keyId,
        amount: order.amount,
        currency: order.currency,
        name: "FundBridge",
        description: order.description,
        order_id: order.orderId,
        handler: async function (response) {
          try {
            await api.patch(`/loans/repayment/${item.id}/pay`, null, {
              params: { razorpayPaymentId: response.razorpay_payment_id },
            });
            load();
          } catch (err) {
            alert(
              err?.response?.data?.message ||
                "Payment succeeded but could not be recorded."
            );
          } finally {
            setPayingId(null);
          }
        },
        modal: {
          ondismiss: function () {
            setPayingId(null);
          },
        },
        prefill: { name: user?.username, email: user?.email },
        theme: { color: "#4f46e5" },
      };
      const rzp = new window.Razorpay(options);
      rzp.open();
    } catch (err) {
      alert(err?.response?.data?.message || "Could not start payment.");
      setPayingId(null);
    }
  };

  if (loading) return <p className="muted center">Loading schedule...</p>;

  return (
    <div className="page">
      <h1>Repayment schedule</h1>
      {error && <div className="alert alert-error">{error}</div>}
      {schedule.length === 0 ? (
        <p className="muted">
          No repayment schedule available yet. It's generated once a loan is
          disbursed.
        </p>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>#</th>
              <th>Due date</th>
              <th>EMI</th>
              <th>Principal</th>
              <th>Interest</th>
              <th>Status</th>
              <th>Paid at</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {schedule.map((item) => (
              <tr key={item.id}>
                <td>{item.installmentNumber}</td>
                <td>{formatDate(item.dueDate)}</td>
                <td>{formatMoney(item.emiAmount)}</td>
                <td>{formatMoney(item.principalComponent)}</td>
                <td>{formatMoney(item.interestComponent)}</td>
                <td>
                  <span className={statusClass(item.status)}>{item.status}</span>
                </td>
                <td>{item.paidAt ? formatDateTime(item.paidAt) : "-"}</td>
                <td>
                  {item.status !== "PAID" && (
                    <button
                      className="btn btn-primary btn-sm"
                      disabled={payingId === item.id}
                      onClick={() => payInstallment(item)}
                    >
                      {payingId === item.id ? "Processing..." : "Pay"}
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
