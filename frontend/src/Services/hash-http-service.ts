import axios from "axios";
import {
    IProgressResponse,
    IResultResponse,
    IStartCalculationResponse
} from "../Types/calculation-responses.ts";

class HashHttpService {
    private readonly baseUrl = 'http://0.0.0.0:8080/hash/'

    async startCalculation(path: string, algorithms: Array<string>): Promise<IStartCalculationResponse> {
        const response = await axios.post(this.baseUrl + "start", {
            path, algorithms
        })

        return response.data
    }

    async getResult(processId: string): Promise<IResultResponse> {
        const response = await axios.get(this.baseUrl + "result", {
            params: {
                processId
            }
        })

        return response.data
    }

    async getProgress(processId: string): Promise<IProgressResponse> {
        const response = await axios.get(this.baseUrl + "progress", {
            params: {
                processId
            }
        })

        return response.data
    }

    async stopProcess(processId: string): Promise<undefined> {
        await axios.post(this.baseUrl + "stop", {
            processId
        })
    }
}

export default new HashHttpService()
