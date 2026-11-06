/** Error thrown for any failed API call; `message` is ready to show to the user. */
export class ApiError extends Error {
  readonly status: number
  readonly fieldErrors: { field: string; message: string }[]

  constructor(status: number, message: string, fieldErrors: { field: string; message: string }[] = []) {
    super(message)
    this.status = status
    this.fieldErrors = fieldErrors
  }
}

type Method = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE'

/**
 * Calls the backend with relative URLs (same origin in every setup: Vite proxy in dev,
 * Nginx in Docker, Spring Boot itself in the native JAR).
 */
export async function request<T>(method: Method, url: string, body?: unknown): Promise<T> {
  const isForm = body instanceof FormData
  let res: Response
  try {
    res = await fetch(url, {
      method,
      headers: isForm ? { Accept: 'application/json' } : { Accept: 'application/json', 'Content-Type': 'application/json' },
      body: body === undefined ? undefined : isForm ? body : JSON.stringify(body),
    })
  } catch {
    throw new ApiError(0, 'Cannot reach the server. Is the backend running?')
  }

  if (res.status === 204) return undefined as T
  const text = await res.text()
  const data = text ? safeJson(text) : undefined

  if (!res.ok) {
    const err = (data ?? {}) as { message?: string; fieldErrors?: { field: string; message: string }[] }
    let message = err.message ?? `${res.status} ${res.statusText}`
    if (err.fieldErrors?.length) {
      message += ': ' + err.fieldErrors.map((f) => `${f.field} ${f.message}`).join(', ')
    }
    throw new ApiError(res.status, message, err.fieldErrors ?? [])
  }
  return data as T
}

function safeJson(text: string): unknown {
  try {
    return JSON.parse(text)
  } catch {
    return undefined
  }
}

export function errorMessage(e: unknown): string {
  return e instanceof Error ? e.message : String(e)
}
