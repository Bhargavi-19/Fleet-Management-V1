import request from "./api";

export function getHealth() {
    return request("/health");
}