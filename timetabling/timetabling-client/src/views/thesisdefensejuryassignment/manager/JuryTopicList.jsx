import React, { useEffect, useState } from 'react'
import { Button, Card, Box, IconButton } from "@mui/material";
import { useHistory } from "react-router-dom";
import ModalCreateThesisDefensePlan from "components/thesisdefensejury/modal/ModalCreateThesisDefensePlan";
import { StandardTable } from "erp-hust/lib/StandardTable";
import PrimaryButton from "components/button/PrimaryButton";
import { request } from "api";
import ModalLoading from "components/common/ModalLoading";
import EditIcon from '@mui/icons-material/Edit';
import KeywordChip from 'components/common/KeywordChip';
import ModalJuryTopic from 'components/thesisdefensejury/modal/ModalJuryTopic';
const JuryTopicList = () => {
    const [loading, setLoading] = useState(true);
    const [juryTopicList, setJuryTopicList] = useState([]);
    const [toggle, setToggle] = React.useState(false);
    const [open, setOpen] = React.useState(false);
    const [action, setAction] = useState({
        topicId: 0,
        type: null,
    })
    const history = useHistory();
    const handleToggle = () => {
        setToggle((prevToggle) => !prevToggle);
    };
    const handleClose = () => {
        console.log(open)
        setOpen((prevOpen) => !prevOpen);
    };
    const columns = [
        { title: "Tên phân ban", field: "name" },
        { title: "Keyword", field: "academicKeywordList", render: (rowData) => rowData?.academicKeywordList?.map((item) => <KeywordChip key={item?.id} keyword={item?.description} />) },
    ];

    useEffect(() => {
        setLoading(true);
        request(
            "GET",
            `/jury-topic/get-all`,
            (res) => {
                setJuryTopicList(res.data);
                setLoading(false)
            }
        ).then();

    }, [toggle]);

    return (
        <Box>
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
                />
            )}
        </Box>
    )
}

export default JuryTopicList