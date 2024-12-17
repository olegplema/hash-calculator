import {useEffect, useState} from "react";
import {hashHttpService} from "../Services";
import {IHashResult} from "../Types/calculation-responses.ts";

export default () => {
    const [filePath, setFilePath] = useState<string>("/Users/oleg/Desktop/largefile.dat")
    const [algorithms, setAlgorithms] = useState<Array<string>>([])
    const [processId, setProcessId] = useState<string>("")
    const [hashes, setHashes] = useState<Array<IHashResult>>([])

    const getResult = async () => {
        const response = await hashHttpService.getResult(processId)
        setHashes(response)
    }

    useEffect(() => {
        if (processId)
            getResult()
    }, [processId])

    const start = async () => {
        if (!filePath || !algorithms.length) return

        const response = await hashHttpService.startCalculation(filePath, algorithms)
        setProcessId(response.processId)
        setFilePath("")
        setAlgorithms([])
        console.log(response)
    }

    const handleCheckboxChange = (label, isChecked) => {
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
        hashes
    }
}
