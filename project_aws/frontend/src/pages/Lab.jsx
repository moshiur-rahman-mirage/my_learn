import { useEffect, useRef, useState } from "react";
import { api } from "../api.js";

/**
 * Lab dashboard: calls /api/lab/whoami every second and keeps the last 30 answers.
 * Behind an ALB you see instanceId/az alternate; during a failover you see red error rows, then recovery.
 */
export default function Lab() {
  const [rows, setRows] = useState([]);
  const [config, setConfig] = useState(null);
  const [running, setRunning] = useState(true);
  const [log, setLog] = useState([]);
  const runningRef = useRef(running);
  runningRef.current = running;

  const note = (msg) => setLog((l) => [`${new Date().toLocaleTimeString()}  ${msg}`, ...l].slice(0, 10));

  useEffect(() => {
    api("/api/lab/config").then(setConfig).catch(() => {});
    const t = setInterval(async () => {
      if (!runningRef.current) return;
      const started = performance.now();
      try {
        const r = await api("/api/lab/whoami");
        r.ms = Math.round(performance.now() - started);
        setRows((prev) => [r, ...prev].slice(0, 30));
      } catch (e) {
        setRows((prev) => [{ time: new Date().toISOString(), error: e.message }, ...prev].slice(0, 30));
      }
    }, 1000);
    return () => clearInterval(t);
  }, []);

  const action = async (label, path) => {
    try {
      const r = await api(path, { method: path.includes("slow") ? "GET" : "POST" });
      note(`${label}: ${JSON.stringify(r)}`);
    } catch (e) {
      note(`${label} failed: ${e.message}`);
    }
  };

  const distinct = new Set(rows.filter((r) => !r.error).map((r) => `${r.instanceId}/${r.host}`));

  return (
    <section>
      <h1>Lab dashboard</h1>
      <div className="panel buttons">
        <button onClick={() => setRunning(!running)}>{running ? "Pause" : "Resume"} polling</button>
        <button onClick={() => action("Stress CPU 180s", "/api/lab/stress?seconds=180")}>Stress CPU (180 s)</button>
        <button className="danger" onClick={() => action("Break health", "/api/lab/health/down")}>Break health</button>
        <button onClick={() => action("Fix health", "/api/lab/health/up")}>Fix health</button>
        <button onClick={() => action("Slow 35s", "/api/lab/slow?seconds=35")}>Slow request (35 s)</button>
        <button onClick={() => setRows([])}>Clear</button>
      </div>
      <p className="muted">
        Note: each button hits whichever instance the load balancer picks, so click several times to reach every instance.
      </p>

      {config && (
        <div className="panel config">
          {Object.entries(config).map(([k, v]) => (
            <span key={k}><b>{k}</b>: <code>{Array.isArray(v) ? v.join(",") || "default" : String(v)}</code></span>
          ))}
        </div>
      )}

      <p>Distinct servers in the last {rows.length} responses: <strong>{distinct.size}</strong></p>

      {log.length > 0 && <pre className="log">{log.join("\n")}</pre>}

      <table className="table lab">
        <thead>
          <tr><th>Time</th><th>Instance</th><th>AZ</th><th>Host</th><th>DB</th><th>Health</th><th>CPU stress</th><th>ms</th></tr>
        </thead>
        <tbody>
          {rows.map((r, i) =>
            r.error ? (
              <tr key={i} className="err-row"><td>{r.time.substring(11, 19)}</td><td colSpan={7}>{r.error}</td></tr>
            ) : (
              <tr key={i}>
                <td>{r.time.substring(11, 19)}</td>
                <td>{r.instanceId}</td>
                <td>{r.az}</td>
                <td>{r.host}</td>
                <td className="db">{r.db}</td>
                <td>{r.healthy ? "UP" : "DOWN"}</td>
                <td>{r.stressing ? "yes" : ""}</td>
                <td>{r.ms}</td>
              </tr>
            )
          )}
        </tbody>
      </table>
    </section>
  );
}
