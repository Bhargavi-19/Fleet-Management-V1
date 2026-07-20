
import { useEffect, useState } from "react";
import { getHealth } from "../services/healthService";

function Home() {

    const [health, setHealth] = useState(null);

    useEffect(() => {

        async function loadHealth() {
            try {
                const data = await getHealth();
                console.log(data);
                setHealth(data);
            } catch (error) {
                console.error(error);
            }
        }

        loadHealth();

    }, []);

    return (
        <div>
            <h1>Fleet Management System</h1>

            <pre>
                {JSON.stringify(health, null, 2)}
            </pre>
        </div>
    );
}

export default Home;