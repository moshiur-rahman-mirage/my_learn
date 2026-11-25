// Single place that knows where the API lives.
export const API_BASE = import.meta.env.VITE_API_BASE ?? "";

export async function api(path, options = {}) {
  const isForm = options.body instanceof FormData;
  const res = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: isForm ? options.headers : { "Content-Type": "application/json", ...(options.headers || {}) },
  });
  if (!res.ok) {
    let msg = `${res.status}`;
    try {
      const body = await res.json();
      msg += ` ${body.detail || body.message || JSON.stringify(body)}`;
    } catch {
      /* not JSON */
    }
    throw new Error(msg);
  }
  if (res.status === 204 || res.status === 202) return null;
  return res.json();
}

/** Local image URLs are relative (/api/images/..); S3 presigned/CloudFront URLs are absolute. */
export function assetUrl(url) {
  if (!url) return null;
  return url.startsWith("/") ? `${API_BASE}${url}` : url;
}

/** Anonymous browser session id for the cart (stored in localStorage). */
export function sessionId() {
  let id = localStorage.getItem("cloudmart.session");
  if (!id) {
    id = crypto.randomUUID();
    localStorage.setItem("cloudmart.session", id);
  }
  return id;
}

/** Fire-and-forget clickstream event (Firehose in Phase 7). */
export function track(event, productId) {
  api("/api/track", {
    method: "POST",
    body: JSON.stringify({ event, productId, sessionId: sessionId() }),
  }).catch(() => {});
}
