import {useEffect, useState} from "react";
import {hashHttpService} from "../Services";
import {IProgressResponse, IResultResponse} from "../Types/calculation-responses.ts";

export default () => {
    const [filePath, setFilePath] = useState<string>("/home/atola/Desktop")
    const [algorithms, setAlgorithms] = useState<Array<string>>([])
    const [processId, setProcessId] = useState<string>("")
    const [hashes, setHashes] = useState<IResultResponse>({hashes: {}})
    const [isLoading, setIsLoading] = useState(false)
    const [progress, setProgress] = useState<IProgressResponse>({
        isStopped: false,
        bytesRead: 0,
        totalBytes: 1
    })

    const reset = () => {
        setProcessId("")
        setHashes({hashes: {}})
        setProgress({
            isStopped: false,
            bytesRead: 0,
            totalBytes: 1
        })
    }

    const stop = () => {
        hashHttpService.stopProcess(processId)
    }

    const getResult = async () => {
        setIsLoading(true)
        const response = await hashHttpService.getResult(processId)
        setHashes(response)
        setIsLoading(false)
    }

    const getProgress = async () => {
        const response = await hashHttpService.getProgress(processId)

        if (response.isStopped) {
            setIsLoading(false)
        }

        setProgress(response)
    }

    useEffect(() => {
        if (processId)
            getResult()
    }, [processId])

    useEffect(() => {
        if (processId && !progress.isStopped && progress.bytesRead !== progress.totalBytes)
            getProgress()
    }, [processId, progress]);

    const start = async () => {
        reset()
        if (!filePath || !algorithms.length) return

        const response = await hashHttpService.startCalculation(filePath, algorithms)
        setProcessId(response.processId)
    }

    const handleCheckboxChange = (label: string, isChecked: boolean) => {
        if (isChecked) {
            setAlgorithms((prevValues) => [...prevValues, label]);
        } else {
            setAlgorithms((prevValues) => prevValues.filter(value => value !== label));
        }
    };

    return {
        setFilePath,
        setAlgorithms,
        start,
        processId,
        filePath,
        algorithms,
        handleCheckboxChange,
        hashes,
        isLoading,
        progress,
        stop
    }
}
