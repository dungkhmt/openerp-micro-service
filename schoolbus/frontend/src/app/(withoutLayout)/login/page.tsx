"use client";
import React from "react";
import { Tabs, Tab, Input, Link, Button, Card, CardBody, CardHeader, Spacer, Divider, TableRow } from "@nextui-org/react";
import { Google, Github, Twitter, FaceBook } from "@/components/icons";
import { useSession, signIn, signOut } from "next-auth/react";
import { SubmitHandler, useForm } from "react-hook-form";
import { useHandleLogin, useHandlerSignup } from "@/services/authService";
import { useRouter } from "next/navigation";


export default function Login() {
    const router = useRouter(); 

    const [selected, setSelected] = React.useState("login");
    const loginMutation = useHandleLogin(
        router
    );
    const {
        register,
        handleSubmit,
        watch,
        formState: { errors },
    } = useForm<ILoginData>()
    const handleLogin: SubmitHandler<ILoginData> = (data) => loginMutation.mutate(data);

    const signUpMutation = useHandlerSignup();
    const {
        register: registerSignUp,
        handleSubmit: handleSubmitSignUp,
        watch: watchSignUp,
        formState: { errors: errorsSignUp },
    } = useForm<ILoginData>()
    const handleSignUp: SubmitHandler<ISignUpData> = (data) => signUpMutation.mutate(data);
    return (
        <div className="flex flex-col w-full items-center justify-center min-h-screen">
            <Card className="max-w-full w-[340px] h-[400px]">
                <CardBody className="overflow-hidden">
                    <Tabs
                        fullWidth
                        size="md"
                        aria-label="Tabs form"
                        selectedKey={selected}
                        color="primary"
                        onSelectionChange={(key: any) => setSelected(key)}
                    >
                        <Tab key="login" title="Login">
                            <form className="flex flex-col gap-4"
                                onSubmit={handleSubmit(handleLogin)}
                            >
                                <Input isRequired label="Email" placeholder="Enter your email" type="email"
                                    {...register("username", { required: true })}
                                />
                                <Input
                                    isRequired
                                    label="Password"
                                    placeholder="Enter your password"
                                    type="password"
                                    {...register("password", { required: true })}
                                />
                                <p className="text-center text-small">
                                    Need to create an account?{" "}
                                    <Link size="sm" onPress={() => setSelected("sign-up")} className="textPrimaryColor">
                                        Sign up
                                    </Link>
                                </p>
                                <div className="flex gap-2 justify-end">
                                    <Button fullWidth color="primary" type="submit">
                                        Login
                                    </Button>
                                </div>
                            </form>
                        </Tab>
                        <Tab key="sign-up" title="Sign up">
                            <form className="flex flex-col gap-4"
                                onSubmit={handleSubmitSignUp(handleSignUp)}
                            >
                                {/* <Input isRequired label="Name" placeholder="Enter your name" type="password" /> */}
                                <Input isRequired label="Email" placeholder="Enter your email" type="email"
                                    {...registerSignUp("username", { required: true })}
                                />
                                <Input
                                    isRequired
                                    label="Password"
                                    placeholder="Enter your password"
                                    type="password"
                                    {...registerSignUp("password", { required: true })}
                                />
                                <p className="text-center text-small">
                                    Already have an account?{" "}
                                    <Link size="sm" onPress={() => setSelected("login")} className="textPrimaryColor">
                                        Login
                                    </Link>
                                </p>
                                <div className="flex gap-2 justify-end">
                                    <Button fullWidth color="primary" type="submit">
                                        Sign up
                                    </Button>
                                </div>
                            </form>
                        </Tab>
                    </Tabs>

                    <Spacer y={1} />
                    <div className="flex items-center justify-center mx-4">
                        <Divider className="flex-grow w-50" />
                        <small className="mx-2">Or</small>
                        <Divider className="flex-grow w-50" />
                    </div>
                    <div className="flex items-center justify-center" style={{ gap: '8px', marginTop: '8px', textAlign: 'center', flexWrap: 'wrap' }}>
                        <Button isIconOnly
                            variant="flat"
                            color="primary"
                        >
                            <a href={process.env.GOOGLE_AUTHORIZE_URL}>
                                <Google />
                            </a>
                        </Button>
                        <Button isIconOnly variant="flat" color="primary">
                            <Github />
                        </Button>
                        <Button isIconOnly variant="flat" color="primary">
                            <Twitter />
                        </Button>
                        <Button isIconOnly variant="flat" color="primary">
                            <FaceBook />
                        </Button>
                    </div>
                </CardBody>
            </Card>
        </div>
    );
}
