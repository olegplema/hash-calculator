import axios from "axios";
import {IHashResult, IStartCalculationResponse} from "../Types/calculation-responses.ts";

class HashHttpService {
    private readonly baseUrl = 'http://0.0.0.0:8080/hash/'

    async startCalculation(path: string, algorithms: Array<string>): Promise<IStartCalculationResponse> {
        const response = await axios.post(this.baseUrl + "start", {
            path, algorithms
        })

        return response.data
    }

    async getResult(processId: string): Promise<Array<IHashResult>> {
        const response = await axios.get(this.baseUrl + "result", {
            params: {
                processId
            }
        })

        return response.data
    }

}

export default new HashHttpService()
