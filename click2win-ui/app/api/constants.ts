declare global {
    interface Window {
      configs: {
        apiUrl: string;
      };
    }
  }
var urlForBrowser;
if (typeof window !== "undefined") {
    urlForBrowser = window?.configs?.apiUrl ? window.configs.apiUrl : "/";
} else {
    urlForBrowser = "/";
}
export const apiUrl = urlForBrowser;