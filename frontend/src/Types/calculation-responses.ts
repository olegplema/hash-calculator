
export interface IStartCalculationResponse {
   processId: string
}

export interface IHashResult {
   algorithm: string
   hash: string
}

export interface IFilesHashes {
   [key: string]:Array<IHashResult>
}

export interface IResultResponse {
   hashes: IFilesHashes
}

export interface IProgressResponse {
   bytesRead: number
   totalBytes: number
   isStopped: boolean
}
