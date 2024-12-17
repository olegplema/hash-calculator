import Button from '@mui/material/Button';
import {Checkbox, FormControlLabel, FormGroup, TextField} from "@mui/material";
import {useCalculation} from "./Hooks";

function App() {
    const controller = useCalculation()

    return (
        <div>
            <div className="flex justify-center gap-1 mt-3">
                <TextField id="outlined-basic"
                           label="File Path"
                           value={controller.filePath}
                           onChange={e => controller.setFilePath(e.target.value)}
                           variant="outlined"
                           className={"w-2/3"}/>
                <Button onClick={controller.start} variant="contained">Start Calculating</Button>
                {/*<Button variant="outlined" color="error">Stop Process</Button>*/}
            </div>
            <div className="flex justify-center">
                <FormGroup row>
                    <FormControlLabel onChange={(_, checked) => controller.handleCheckboxChange("MD5", checked)}
                                      control={<Checkbox />}
                                      label="MD5" />
                    <FormControlLabel onChange={(_, checked) => controller.handleCheckboxChange("SHA-1", checked)}
                                      control={<Checkbox />}
                                      label="SHA-1" />
                    <FormControlLabel onChange={(_, checked) => controller.handleCheckboxChange("SHA-256", checked)}
                                      control={<Checkbox />}
                                      label="SHA-256" />
                </FormGroup>
            </div>
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
