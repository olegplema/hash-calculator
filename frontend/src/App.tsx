import Button from '@mui/material/Button';
import {Checkbox, FormControlLabel, FormGroup, TextField} from "@mui/material";
import {useCalculation} from "./Hooks";
import CircularProgressWithLabel from "./Components/CircularProgressWithLabel.tsx";

function App() {
    const controller = useCalculation()

    const progress = controller.progress.bytesRead / controller.progress.totalBytes * 100

    return (
        <div>
            <div className="flex justify-center gap-1 mt-3">
                <TextField id="outlined-basic"
                           label="File Path"
                           value={controller.filePath}
                           onChange={e => controller.setFilePath(e.target.value)}
                           variant="outlined"
                           className={"w-2/3"}/>

                {controller.isLoading
                    ?<Button onClick={controller.stop} variant="outlined" color="error">Stop Process</Button>
                    :<Button onClick={controller.start} variant="contained">Start Calculating</Button>
                }
            </div>
            <div className="flex justify-center">
                <FormGroup row>
                    <FormControlLabel onChange={(_, checked) => controller.handleCheckboxChange("MD5", checked)}
                                      control={<Checkbox/>}
                                      label="MD5"/>
                    <FormControlLabel onChange={(_, checked) => controller.handleCheckboxChange("SHA-1", checked)}
                                      control={<Checkbox/>}
                                      label="SHA-1"/>
                    <FormControlLabel onChange={(_, checked) => controller.handleCheckboxChange("SHA-256", checked)}
                                      control={<Checkbox/>}
                                      label="SHA-256"/>
                </FormGroup>
            </div>
            {controller.isLoading &&
                <div className="flex justify-center">
                    <CircularProgressWithLabel value={Math.round(progress)}/>
                </div>
            }
            {controller.progress.isStopped &&
                <div className="flex justify-center">
                    <h2 className="text-red-700">Process is stopped</h2>
                </div>
            }
            <div className="flex items-center flex-col">
                <div>
                    {controller.hashes.map(hash => <div key={hash.algorithm + hash.hash} className={"flex flex-row"}>
                        <div>{hash.algorithm + ":"}</div>
                        <div>{hash.hash}</div>
                    </div>)}
                </div>
            </div>
        </div>
    )
}

export default App
