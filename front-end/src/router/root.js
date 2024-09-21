const { Suspense, lazy } = require("react");
const { createBrowserRouter } = require("react-router-dom");

const loading = <div>Loading...</div>
const Main = lazy(() => import("../page/MainPage"));
const Login = lazy(() => import("../page/LoginPage"));
const Post = lazy(() => import("../page/PostPage"));
const AddPost = lazy(() => import("../page/AddPostPage"));
const Signup = lazy(() => import("../page/SignupPage"));

const root = createBrowserRouter([
    {
        path: "",
        element: <Suspense fallback={loading}><Main/></Suspense>
    },
    {
        path: "login",
        element: <Suspense fallback={loading}><Login/></Suspense>
    },
    {
        path: "post/:postId",
        element: <Suspense fallback={loading}><Post/></Suspense>
    },
    {
        path: "add-post",
        element: <Suspense fallback={loading}><AddPost/></Suspense>
    },
    {
        path: "signup",
        element: <Suspense fallback={loading}><Signup/></Suspense>
    }
]);

export default root;