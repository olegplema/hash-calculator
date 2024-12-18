
export interface IStartCalculationResponse {
   processId: string
}

export interface IHashResult {
   algorithm: string
   hash: string
}

export interface IProgressResponse {
   bytesRead: number
   totalBytes: number
   isStopped: boolean
}
