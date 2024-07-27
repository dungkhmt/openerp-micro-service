import React, { useEffect, useState } from 'react'
import { Button, Card, Box, IconButton, FormControl, InputLabel, Select, MenuItem } from "@mui/material";
import { useHistory } from "react-router-dom";
import ModalCreateThesisDefensePlan from "components/thesisdefensejury/modal/ModalCreateThesisDefensePlan";
import { StandardTable } from "erp-hust/lib/StandardTable";
import PrimaryButton from "components/button/PrimaryButton";
import { request } from "api";
import ModalLoading from "components/common/ModalLoading";
import KeywordChip from 'components/common/KeywordChip';
import ModalJuryTopic from 'components/thesisdefensejury/modal/ModalJuryTopic';
import useQuery from 'hooks/useQuery';
// Màn quản lý phân ban đợt BVĐA
const JuryTopicList = () => {
    const [loading, setLoading] = useState(true);
    const [juryTopicList, setJuryTopicList] = useState([]);
    const [currentThesisDefensePlan, setCurrentThesisDefensePlan] = useState();
    const [thesisDefensePlanList, setThesisDefensePlanList] = useState([]);
    const [toggle, setToggle] = React.useState(false);
    const [open, setOpen] = React.useState(false);
    const [action, setAction] = useState({
        topicId: 0,
        type: null,
    })
    let query = useQuery();
    const plan = query?.get('id');
    const history = useHistory();
    const handleToggle = () => {
        setToggle((prevToggle) => !prevToggle);
    };
    const handleClose = () => {
        setOpen((prevOpen) => !prevOpen);
    };

    const handleChange = (e) => {
        const planId = e?.target?.value;
        history?.push(`?id=${planId}`);
        setCurrentThesisDefensePlan(planId)
    };

    const columns = [
        { title: "Tên phân ban", field: "name" },
        { title: "Keyword", field: "academicKeywordList", render: (rowData) => rowData?.academicKeywordList?.map((item) => <KeywordChip key={item?.id} keyword={item?.description} />) },
        { title: "Điều phối viên", field: "teacher", render: (rowData) => rowData?.teacher?.teacherName },
    ];
    // useEffect(() => {
    //     setLoading(true);
    //     if (plan) {

    //         setCurrentThesisDefensePlan(plan?.replaceAll("_", " "));
    //     }
    // }, [plan]);

    useEffect(() => {
        setLoading(true);
        request(
            "GET",
            `/jury-topic/thesis-defense-plan`,
            (res) => {
                setThesisDefensePlanList(res?.data);

                setLoading(false)
            }
        ).then();
        if (plan) {
            request("GET",
                `/jury-topic/${plan}`,
                (res) => {
                    console.log(res?.data)
                    setJuryTopicList(res?.data);
                    setLoading(false)
                }
            ).then();
        }
    }, [toggle, plan]);

    useEffect(() => {
        console.log(currentThesisDefensePlan);
        request("GET",
            `/jury-topic/${currentThesisDefensePlan}`,
            (res) => {
                console.log(res?.data)
                setJuryTopicList(res?.data);
                setLoading(false)
            }
        ).then();
    }, [currentThesisDefensePlan])
    return (
        <Box>
            <FormControl sx={{ width: '30%' }}>
                <InputLabel id="thesisDefensePlan">Chọn đợt bảo vệ đồ án</InputLabel>
                <Select
                    labelId="thesisDefensePlan"
                    id="thesisDefensePlanSelect"
                    value={currentThesisDefensePlan}
                    label="thesisDefensePlan"
                    onChange={handleChange}
                >
                    {thesisDefensePlanList?.map(({ id, name }) =>
                        <MenuItem key={id} value={id}>
                            {name}
                        </MenuItem>
                    )}
                </Select>
            </FormControl>
            <Box
                display={"flex"}
                flexDirection={"row-reverse"}
                marginTop={3}
                paddingRight={6}
            >
                <PrimaryButton onClick={() => {
                    setAction({ topicId: null, type: "CREATE" })
                    setOpen((prevOpen) => !prevOpen)
                }}>
                    Tạo phân ban mới
                </PrimaryButton>
            </Box>
            {loading && <ModalLoading loading={loading} />}
            <StandardTable
                title={"Danh sách phân ban"}
                data={juryTopicList}
                columns={columns}
                options={{
                    selection: false,
                    pageSize: 20,
                    search: true,
                    sorting: true,
                }}
            />
            {open && (
                <ModalJuryTopic
                    open={open}
                    type={action.type}
                    topicId={action.topicId}
                    handleClose={handleClose}
                    handleToggle={handleToggle}
                    thesisDefensePlanId={currentThesisDefensePlan}
                />
            )}
        </Box>
    )
}

export default JuryTopicList