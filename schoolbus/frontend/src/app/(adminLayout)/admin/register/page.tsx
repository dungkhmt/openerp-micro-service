"use client";
import CustomSkeleton from '@/components/custom-skeleton';
import { SearchIcon } from '@/components/icons/searchicon';
import {
    Autocomplete,
    AutocompleteItem,
    Button,
    Input,
    RadioGroup,
    Switch,
    Tab,
    Table,
    TableBody,
    TableCell,
    TableColumn,
    TableHeader,
    TableRow,
    Modal,
    ModalContent,
    ModalHeader,
    ModalBody,
    ModalFooter,
    Pagination,
    Select,
    SelectItem,
    useDisclosure,
    Tabs,
    Chip,
} from '@nextui-org/react';
import dynamic from 'next/dynamic';
import React, { useMemo } from 'react';
import { useGetAutoComplete, useGetSearch, useGetDirections } from '@/services/mapService';
import _, { set } from 'lodash';
import LocationIcon from '@/components/icons/location-icon';
import { useGetStudentRides } from '@/services/client/parentStudentService';
import { useAddRequestRegistration, useGetRequestRegistration } from '@/services/client/clientRequestRegistrationService';
import { on } from 'events';
import { useGetListPickupPoint } from '@/services/admin/pickupPointService';
import { useGetPageRequestRegistration, useHandleRequestRegistration } from '@/services/admin/requestRegistrationService';
import { request_registration_status_map } from '@/util/constant';
import { toast } from "react-toastify";
import { validateColor } from '@/util/color';


const RegisterPage: React.FC = () => {

    // get auto complete
    const [autoCompleteQuery, setAutoCompleteQuery] = React.useState('');
    let autoCompleteParams: IAutoCompleteParams = {
        text: autoCompleteQuery,
        boundary: {
            country: 'VNM'
        }
    }
    const debounceSetAutoCompleteQuery = _.debounce((value: string) => setAutoCompleteQuery(value), 500);
    const { data: autoCompleteData, isLoading: autoCompleteLoading, error: autoCompleteError } = useGetAutoComplete(autoCompleteParams);
    const [selectedAutoCompleteData, setSelectedAutoCompleteData] = React.useState<IFeature | null>(null);

    // get directions
    const useGetDirectionsParams: IDirectionsParams = {
        coordinates: [[105.804817, 21.028511], [105.803577, 21.03422], [105.80325113694303, 21.03586062789824]]
    }
    const { data: directionsGetResponse, isLoading: directionsLoading, error: directionsError } = useGetDirections(useGetDirectionsParams);

    // get list pickup point
    const [addressQuery, setAddressQuery] = React.useState('');
    let pickupPointParams: IGetListPickupPointParams = {
        address: addressQuery
    }
    const { data: listPickupPoint, isLoading: listPickupPointLoading, error: listPickupPointError } = useGetListPickupPoint(pickupPointParams);

    // enable click point to map (true) or enable pick up point (false)
    const [enableClickMap, setEnableClickMap] = React.useState<boolean>(false);

    const Map = useMemo(() => dynamic(
        () => import('@/components/map/MapAdminRegister'),
        {
            loading: () => <CustomSkeleton />,
            ssr: false
        }
    ), [])

    // get page request registration
    const [studentName, setStudentName] = React.useState<string | null>(null);
    const [parentName, setParentName] = React.useState<string | null>(null);
    const [statuses, setStatuses] = React.useState<string | null>(null);
    const [address, setAddress] = React.useState<string | null>(null);
    const [page, setPage] = React.useState(1);
    const pageRequestRegistrationParams: IPageRequestRegistrationParams = {
        studentName: studentName,
        parentName: parentName,
        statuses: statuses,
        address: address,
        page: page - 1,
        size: 10,
        sort: '-createdAt'
    }
    const { data: pageRequestRegistrationData, isLoading: pageRequestRegistrationLoading, error: pageRequestRegistrationError } = useGetPageRequestRegistration(pageRequestRegistrationParams);
    const bottomContent = (
        <div className="py-2 px-2 flex w-full justify-center items-center">
            <Pagination
                isCompact
                showControls
                showShadow
                color="primary"
                page={page}
                total={pageRequestRegistrationData?.result.totalPages || 1}
                onChange={setPage}
            />
        </div>
    );

    // selected pending request registration
    const [selectedPendingRequestRegistration, setSelectedPendingRequestRegistration] = React.useState<IRequestRegistrationResponse[] | null>(null);

    // get pending page request registration
    const [studentNamePending, setStudentNamePending] = React.useState<string | null>(null);
    const [parentNamePending, setParentNamePending] = React.useState<string | null>(null);
    const [addressPending, setAddressPending] = React.useState<string | null>(null);
    const [pendingPage, setPendingPage] = React.useState(1);
    const pendingPageRequestRegistrationParams: IPageRequestRegistrationParams = {
        studentName: studentNamePending,
        parentName: parentNamePending,
        statuses: 'PENDING',
        address: addressPending,
        page: pendingPage - 1,
        size: 5,
        sort: '-createdAt'
    }
    const { data: pendingPageRequestRegistrationData, isLoading: pendingPageRequestRegistrationLoading, error: pendingPageRequestRegistrationError } = useGetPageRequestRegistration(pendingPageRequestRegistrationParams);
    const pendingBottomContent = (
        <div className="py-2 px-2 flex w-full justify-center items-center">
            <Pagination
                isCompact
                showControls
                showShadow
                color="primary"
                page={pendingPage}
                total={pendingPageRequestRegistrationData?.result.totalPages || 1}
                onChange={setPendingPage}
            />
        </div>
    );

    // handle request registration
    const { isOpen: isOpenHandleRequestRegistration, onOpen: onOpenHandleRequestRegistration, onOpenChange: onOpenChangeHandleRequestRegistration } = useDisclosure();
    const [requestIds, setRequestIds] = React.useState<number[] | null>(null);
    const [handleStatus, setHandleStatus] = React.useState<string | null>('ACCEPTED');
    const [pickupPointId, setPickupPointId] = React.useState<number | null>(null);
    const [addressHandle, setAddressHandle] = React.useState<string | null>(null);
    const [latitude, setLatitude] = React.useState<number | null>(null);
    const [longitude, setLongitude] = React.useState<number | null>(null);
    const [note, setNote] = React.useState<string | null>(null);
    const handleRequestRegistrationParams: IHandleRequestRegistrationRequest = {
        requestIds: requestIds,
        status: handleStatus,
        pickupPointId: pickupPointId,
        address: addressHandle,
        latitude: latitude,
        longitude: longitude,
        note: note
    }
    // set addressHandle, latitude, longitude follow selectedAutoCompleteData
    React.useEffect(() => {
        if (selectedAutoCompleteData) {
            setAddressHandle(selectedAutoCompleteData.properties.name + ', ' + selectedAutoCompleteData.properties.county + ', ' + selectedAutoCompleteData.properties.region + ', ' + selectedAutoCompleteData.properties.country);
            setLatitude(selectedAutoCompleteData.geometry.coordinates[1]);
            setLongitude(selectedAutoCompleteData.geometry.coordinates[0]);
        }
    }, [selectedAutoCompleteData]);
    const handleRequestRegistrationMutation = useHandleRequestRegistration(() => {
        onOpenChangeHandleRequestRegistration();
    })
    const handleHandleRequestRegistration = () => {
        // validate before handle request registration
        if (handleStatus == 'REJECTED' && !note) {
            // alert('Vui lòng nhập lý do từ chối');
            toast.error('Vui lòng nhập lý do từ chối');
            return;
        }
        if (!requestIds) {
            // alert('Vui lòng chọn yêu cầu đăng ký');
            toast.error('Vui lòng chọn yêu cầu đăng ký');
            return;
        }
        if (handleStatus == 'ACCEPTED' && (!pickupPointId && !(addressHandle && longitude && latitude))) {
            // alert('Vui lòng chọn điểm đón');
            toast.error('Vui lòng chọn điểm đón');
            return;
        }
        handleRequestRegistrationMutation.mutate(handleRequestRegistrationParams);
    }
    // pick available pickup point

    return (
        <div className='flex flex-col'>
            <div className='flex justify-between w-auto'>
                <div className='w-5/12 mr-4'>

                    <h5 className='text-xl font-bold m-1'>Yêu cầu đăng ký chờ xử lý</h5>

                    <div className='flex justify-between'>
                        <div className='flex gap-2 mx-2 mt-2'>
                            <Input
                                size='sm'
                                placeholder='Tên học sinh'
                                onChange={(e) => setStudentNamePending(e.target.value)}
                            />
                            <Input
                                size='sm'
                                placeholder='Tên phụ huynh'
                                onChange={(e) => setParentNamePending(e.target.value)}
                            />
                            <Input
                                size='sm'
                                placeholder='Địa chỉ'
                                onChange={(e) => setAddressPending(e.target.value)}
                            />
                        </div>
                    </div>

                    <div className='m-2'>
                        <Table
                            selectionMode='multiple'
                            bottomContent={pendingBottomContent}
                            onSelectionChange={(selectedKeys) => {
                                // set selected pending request registration based on selected keys & value of pendingPageRequestRegistrationData
                                if (selectedKeys === 'all') {
                                    setSelectedPendingRequestRegistration(
                                        pendingPageRequestRegistrationData?.result.content ?? []
                                    );
                                    setRequestIds((pendingPageRequestRegistrationData?.result.content ?? []).filter(item => item.requestRegistration.status === 'PENDING').map(item => item.requestRegistration.id));
                                } else {
                                    const keysArray = Array.from(selectedKeys).map(Number);
                                    setSelectedPendingRequestRegistration(
                                        (pendingPageRequestRegistrationData?.result.content ?? []).filter(
                                            (item) => keysArray.includes(item.requestRegistration.id)
                                        )
                                    );
                                    setRequestIds(keysArray);
                                }
                            }}
                        >
                            <TableHeader columns={[
                                { key: 'student', label: 'Học sinh' },
                                { key: 'parent', label: 'Phụ huynh' },
                                { key: 'status', label: 'Trạng thái' },
                                { key: 'address', label: 'Địa chỉ' },
                            ]}>
                                {(column) => <TableColumn key={column.key}>{column.label}</TableColumn>}
                            </TableHeader>

                            <TableBody
                                emptyContent='No row to display'
                                items={(pendingPageRequestRegistrationData?.result.content ?? []).filter(item => item.requestRegistration.status === 'PENDING')}>
                                {(item) => (
                                    <TableRow key={item.requestRegistration.id}>
                                        <TableCell>{item.student.name}</TableCell>
                                        <TableCell>{item.parent.name}</TableCell>
                                        <TableCell>
                                            <Chip
                                                variant='flat'
                                                color={
                                                    item.requestRegistration.status === 'PENDING' ? 'warning' :
                                                        item.requestRegistration.status === 'ACCEPTED' ? 'success' :
                                                            item.requestRegistration.status === 'REJECTED' ? 'danger' : 'default'

                                                }
                                            >
                                                {item.requestRegistration.status}
                                            </Chip>
                                        </TableCell>
                                        <TableCell>{item.requestRegistration.address}</TableCell>
                                    </TableRow>
                                )}
                            </TableBody>
                        </Table>
                    </div>

                    <div className='flex justify-between m-2'>
                        <Tabs
                            onSelectionChange={(selected) => setHandleStatus(selected.toString())}
                            color={handleStatus === 'ACCEPTED' ? 'success' : 'danger'}
                        >
                            <Tab key='ACCEPTED' title='Chấp nhận' />
                            <Tab key='REJECTED' title='Từ chối' />
                        </Tabs>

                        {!(handleStatus == 'REJECTED') && (
                            <Switch
                                checked={enableClickMap}
                                onChange={() => {
                                    setEnableClickMap(!enableClickMap)
                                    if (enableClickMap) {
                                        setPickupPointId(null);
                                    }
                                }}
                            >
                                {enableClickMap ? 'Chọn trên map' : 'Chọn điểm đón'}
                            </Switch>
                        )}
                    </div>

                    {enableClickMap && !(handleStatus == 'REJECTED') && (
                        <Autocomplete
                            placeholder="Nhập địa điểm"
                            allowsCustomValue
                            startContent={<SearchIcon />}
                            items={autoCompleteData?.features.map((item) => item.properties) || []}
                            className="m-2 w-full"
                            selectorIcon={false}
                            onInputChange={(value) => {
                                debounceSetAutoCompleteQuery(value);
                            }}
                            onSelectionChange={
                                (selectedId) => {
                                    const selectedItem = autoCompleteData?.features.find((item) => item.properties.id === selectedId);
                                    if (selectedItem) {
                                        setSelectedAutoCompleteData(selectedItem);
                                    }
                                }
                            }
                            isClearable={false}
                        >
                            {(item) =>
                                <AutocompleteItem
                                    key={item.id}
                                    textValue={`${item.name ?? ''}, ${item.county ?? ''}, ${item.region ?? ''}, ${item.country ?? ''}`}
                                >
                                    <div className="flex gap-2 items-center">
                                        <div className='flex-shrink-0'>
                                            <LocationIcon />
                                        </div>

                                        <div className='flex flex-col'>
                                            {`${item.name ?? ''}, ${item.county ?? ''}, ${item.region ?? ''}, ${item.country ?? ''}`}
                                        </div>
                                    </div>

                                </AutocompleteItem>
                            }
                        </Autocomplete>
                    )}

                    <div className='flex justify-end mx-2'>
                        <Button
                            onClick={onOpenHandleRequestRegistration}
                            color="primary"
                        >
                            Xử lý yêu cầu
                        </Button>
                    </div>

                </div>

                {/* map */}
                <div
                    className="w-7/12 z-50"
                >
                    <Map
                        features={selectedAutoCompleteData ? [selectedAutoCompleteData] : []}
                        setSelectedAutoCompleteData={setSelectedAutoCompleteData}
                        pickupPoints={listPickupPoint?.result.content ?? []}
                        directionsGetResponse={directionsGetResponse}
                        enableClickMap={enableClickMap}
                        selectedPendingRequestRegistration={selectedPendingRequestRegistration}
                        pickupPointId={pickupPointId}
                        setPickupPointId={setPickupPointId}
                    />
                </div >
            </div>

            {/* table all request registration */}
            <div className='m-4'>
                <h3 className='text-xl font-bold m-2'>Lịch sử đăng ký</h3>
                <div className='flex gap-2 m-2'>
                    <Input
                        size='sm'
                        placeholder='Tên học sinh'
                        onChange={(e) => setStudentName(e.target.value)}
                    />
                    <Input
                        size='sm'
                        placeholder='Tên phụ huynh'
                        onChange={(e) => setParentName(e.target.value)}
                    />
                    <Input
                        size='sm'
                        placeholder='Địa chỉ đăng ký'
                        onChange={(e) => setAddress(e.target.value)}
                    />
                    <Select
                        size='sm'
                        placeholder='Trạng thái'
                        selectionMode='multiple'
                        color={validateColor(request_registration_status_map.find((status) => status.value === statuses)?.color || 'default')}
                        onChange={(event: React.ChangeEvent<HTMLSelectElement>) => {
                            const newValue = event.target.value;
                            setStatuses(newValue);
                        }}
                    >
                        {request_registration_status_map.map((status) => (
                            <SelectItem key={status.value} value={status.value} color={validateColor(status.color)}>
                                {status.label}
                            </SelectItem>
                        ))}
                    </Select>
                </div>

                <Table
                    selectionMode='single'
                    bottomContent={bottomContent}
                >
                    <TableHeader columns={[
                        { key: 'student', label: 'Học sinh' },
                        { key: 'parent', label: 'Phụ huynh' },
                        { key: 'status', label: 'Trạng thái' },
                        { key: 'address', label: 'Địa chỉ đăng ký' },
                        { key: 'note', label: 'Ghi chú' },
                    ]}>
                        {(column) => <TableColumn key={column.key}>{column.label}</TableColumn>}
                    </TableHeader>

                    <TableBody items={(pageRequestRegistrationData?.result.content ?? [])} emptyContent='No row to display'>
                        {(item) => (
                            <TableRow key={item.requestRegistration.id}>
                                <TableCell>{item.student.name}</TableCell>
                                <TableCell>{item.parent.name}</TableCell>
                                <TableCell>
                                    <Chip
                                        variant='flat'
                                        color={
                                            item.requestRegistration.status === 'PENDING' ? 'warning' :
                                                item.requestRegistration.status === 'ACCEPTED' ? 'success' :
                                                    item.requestRegistration.status === 'REJECTED' ? 'danger' : 'default'
                                        }
                                    >
                                        {item.requestRegistration.status}
                                    </Chip>
                                </TableCell>
                                <TableCell>{item.requestRegistration.address}</TableCell>
                                <TableCell>{item.requestRegistration.note}</TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </div>

            {/* modal handle request registration */}
            <div className='relative z-10'>
                <Modal isOpen={isOpenHandleRequestRegistration} onOpenChange={onOpenChangeHandleRequestRegistration}
                >
                    <ModalContent>
                        {(onOpenAddRegistrationConfirm) => (
                            <>
                                <ModalHeader className="flex flex-col gap-1">Xác nhận xử lý yêu cầu?</ModalHeader>
                                <ModalBody>
                                    <p>
                                        {handleStatus == 'ACCEPTED' ? 'Chấp nhận yêu cầu đăng ký?' : 'Từ chối yêu cầu đăng ký?'}
                                    </p>
                                    {/* just input note */}
                                    {handleStatus == 'REJECTED' && (
                                        <Input
                                            placeholder='Lý do từ chối'
                                            onChange={(e) => setNote(e.target.value)}
                                        />
                                    )}
                                </ModalBody>
                                <ModalFooter>
                                    <Button color="danger" variant="light"
                                        onPress={onOpenChangeHandleRequestRegistration}
                                    >
                                        Huỷ
                                    </Button>
                                    <Button color="primary" onPress={() => {
                                        handleHandleRequestRegistration();
                                    }}>
                                        Xác nhận
                                    </Button>
                                </ModalFooter>
                            </>
                        )}
                    </ModalContent>
                </Modal>
            </div>
        </div>
    );
};

export default RegisterPage;